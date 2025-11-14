package twocheg.mod.settings

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.command.argument.BlockStateArgument
import net.minecraft.command.argument.BlockStateArgumentType
import net.minecraft.registry.Registries
import net.minecraft.text.Text
import net.minecraft.util.Identifier

// интересный факт, этот класс был полностью взят со старой java версии чита и переписан на kotlin автоматически редактором и с того момента не тронут
class BlockSelectCmd(
    name: String,
    cmdName: String,
    defaultValue: List<String> = emptyList()
) : Setting<List<String>>(name, defaultValue) {
    init {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, registryAccess ->
            dispatcher.register(
                ClientCommandManager.literal(cmdName)
                    .then(
                        ClientCommandManager.literal("blocksList")
                            .then(
                                ClientCommandManager.literal("clear")
                                    .executes {
                                        onBlocksClear()
                                        sendMessage("blocks list is clear")
                                        1
                                    }
                            )
                            .then(
                                ClientCommandManager.literal("list")
                                    .executes {
                                        val ids = getValue()
                                        if (ids.isEmpty()) {
                                            sendMessage("blocks list is empty")
                                        } else {
                                            ids.forEach { blockId ->
                                                getBlockName(blockId)?.let { nameText ->
                                                    MinecraftClient.getInstance().inGameHud.chatHud.addMessage(nameText)
                                                } ?: run {
                                                    sendMessage("unknown block: $blockId")
                                                }
                                            }
                                        }
                                        1
                                    }
                            )
                            .then(
                                ClientCommandManager.literal("add")
                                    .then(
                                        ClientCommandManager.argument("block", BlockStateArgumentType.blockState(registryAccess))
                                            .executes { context ->
                                                val blockArg = context.getArgument("block", BlockStateArgument::class.java)
                                                val blockState = blockArg.blockState
                                                val blockId = blockState.block.translationKey
                                                val convertedId = convertId(blockId)

                                                onBlocksAdd(listOf(convertedId))

                                                sendMessage("added:")
                                                getBlockName(blockId)?.let { nameText ->
                                                    MinecraftClient.getInstance().inGameHud.chatHud.addMessage(nameText)
                                                }
                                                1
                                            }
                                    )
                            )
                            .then(
                                ClientCommandManager.literal("remove")
                                    .then(
                                        ClientCommandManager.argument("block", BlockStateArgumentType.blockState(registryAccess))
                                            .executes { context ->
                                                val blockArg = context.getArgument("block", BlockStateArgument::class.java)
                                                val blockState = blockArg.blockState
                                                val blockId = blockState.block.translationKey
                                                val convertedId = convertId(blockId)

                                                onBlocksRemove(listOf(convertedId))

                                                sendMessage("remove:")
                                                getBlockName(blockId)?.let { nameText ->
                                                    MinecraftClient.getInstance().inGameHud.chatHud.addMessage(nameText)
                                                }
                                                1
                                            }
                                    )
                            )
                    )
            )
        }
    }

    private fun sendMessage(text: String) {
        MinecraftClient.getInstance().inGameHud.chatHud.addMessage(Text.literal(text))
    }

    private fun getBlockName(blockId: String): Text? {
        val idString = convertId(blockId)
        val id = Identifier.of(idString)
        val block = Registries.BLOCK[id]
        return if (block != Blocks.AIR) block.name else null
    }

    fun onBlocksAdd(ids: Collection<String>) {
        val newList = getValue().toMutableList()
        newList.addAll(ids)
        setValue(newList)
    }

    private fun onBlocksRemove(ids: Collection<String>) {
        val newList = getValue().toMutableList()
        newList.removeAll(ids)
        setValue(newList)
    }

    private fun onBlocksClear() {
        setValue(emptyList<Any>())
    }

    companion object {
        fun convertId(id: String): String {
            return id.replace("block.", "").replace(".", ":")
        }
    }
}