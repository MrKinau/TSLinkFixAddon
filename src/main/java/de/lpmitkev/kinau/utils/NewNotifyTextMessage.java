package de.lpmitkev.kinau.utils;

import net.labymod.ingamegui.Module;
import net.labymod.ingamegui.modules.TeamSpeakModule;
import net.labymod.teamspeak.backend.TeamspeakClient;
import net.labymod.teamspeak.backend.TeamspeakEnumChatType;
import net.labymod.teamspeak.backend.TeamspeakServer;
import net.labymod.teamspeak.backend.listener.Listener;
import net.labymod.teamspeak.backend.listener.notify.NotifyTextMessage;
import net.labymod.teamspeak.frontend.TeamSpeakTextDesign;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;

import java.lang.reflect.Field;

public class NewNotifyTextMessage extends NotifyTextMessage {

    @Override
    public void handle(String message) {
        try {
            Field listenerField = Listener.class.getDeclaredField("lastMessage");
            listenerField.setAccessible(true);
            listenerField.set(this, message);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


        int serverId = this.getArgument("schandlerid").parseInt();
        String msg = this.getArgument("msg").getValue();
        int invoker = this.getArgument("invokerid").parseInt();
        int targetMode = this.getArgument("targetmode").parseInt();
        int target = this.getArgument("target") == null ? -1 : this.getArgument("target").parseInt();
        TeamspeakServer server = TeamspeakServer.getById(serverId);
        TeamspeakClient senderClient = server.getClientById(invoker);
        String formattedMSG;
        if (senderClient == null) {
            int messageByMe = this.getArgument("invokerid").parseInt();
            formattedMSG = this.getArgument("invokername").getValue();
            senderClient = new TeamspeakClient(messageByMe, formattedMSG, server);
            server.getClientList().put(Integer.valueOf(senderClient.getClientId()), senderClient);
        }

        boolean messageByMe1 = senderClient.getClientId() == server.getMe();
        formattedMSG = TeamSpeakTextDesign.formatToMC(msg);
        String textMessage = senderClient.getClientName() + ": " + formattedMSG;
        if (targetMode != 3 && ((TeamSpeakModule) Module.getModuleByClass(TeamSpeakModule.class)).isDisplayMessagesInMCChat() && invoker != server.getMe()) {
            String targetClient = ModColor.cl("7") + "[" + ModColor.cl("9") + "TeamSpeak" + ModColor.cl("7") + "] ";
            String chatMessage = targetClient + ModColor.cl("7") + senderClient.getClientName() + ModColor.cl("8") + ": " + ModColor.cl("f") + formattedMSG;
            chatMessage = chatMessage.replace(" §9", "§9 ");
            chatMessage = chatMessage.replace(" §r§9", "§9 ");
            chatMessage = chatMessage.replace(" §f§9", "§9 ");
            Minecraft.getMinecraft().thePlayer.addChatMessage(URLDetection.newChatWithLinks(chatMessage, true));
        }

        switch (targetMode) {
            case 1:
                TeamspeakClient targetClient1 = server.getClientById(target);
                server.addToChat(TeamspeakEnumChatType.PRIVATE, textMessage, senderClient, targetClient1, messageByMe1);
                break;
            case 2:
                server.addToChat(TeamspeakEnumChatType.CHANNEL, textMessage, senderClient, (TeamspeakClient) null, messageByMe1);
                break;
            case 3:
                server.addToChat(TeamspeakEnumChatType.SERVER, textMessage, senderClient, (TeamspeakClient) null, messageByMe1);
        }

    }
}
