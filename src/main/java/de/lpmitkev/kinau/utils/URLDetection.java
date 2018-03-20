package de.lpmitkev.kinau.utils;

import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLDetection {

    //Copied from ForgeHooks.java
    static final Pattern URL_PATTERN = Pattern.compile(
            //         schema                          ipv4            OR           namespace                 port     path         ends
            //   |-----------------|        |-------------------------|  |----------------------------|    |---------| |--|   |---------------|
            "((?:[a-z0-9]{2,}:\\/\\/)?(?:(?:[0-9]{1,3}\\.){3}[0-9]{1,3}|(?:[-\\w_\\.]{1,}\\.[a-z]{2,}?))(?::[0-9]{1,5})?.*?(?=[!\"\u00A7 \n]|$))",
            Pattern.CASE_INSENSITIVE);

    //Copied from ForgeHooks.java
    public static IChatComponent newChatWithLinks(String string, boolean allowMissingHeader) {
        // Includes ipv4 and domain pattern
        // Matches an ip (xx.xxx.xx.xxx) or a domain (something.com) with or
        // without a protocol or path.
        IChatComponent ichat = null;
        Matcher matcher = URL_PATTERN.matcher(string);
        int lastEnd = 0;

        // Find all urls
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            // Append the previous left overs.
            String part = string.substring(lastEnd, start);
            if (part.length() > 0) {
                if (ichat == null)
                    ichat = new ChatComponentText(part);
                else
                    ichat.appendText(part);
            }
            lastEnd = end;
            String url = string.substring(start, end);
            IChatComponent link = new ChatComponentText(url);

            try {
                // Add schema so client doesn't crash.
                if ((new URI(url)).getScheme() == null) {
                    if (!allowMissingHeader) {
                        if (ichat == null)
                            ichat = new ChatComponentText(url);
                        else
                            ichat.appendText(url);
                        continue;
                    }
                    url = "http://" + url;
                }
            } catch (URISyntaxException e) {
                // Bad syntax bail out!
                if (ichat == null) ichat = new ChatComponentText(url);
                else ichat.appendText(url);
                continue;
            }

            // Set the click event and append the link.
            ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
            link.getChatStyle().setChatClickEvent(click);
            link.getChatStyle().setUnderlined(true);
            link.getChatStyle().setColor(EnumChatFormatting.BLUE);
            if (ichat == null)
                ichat = link;
            else
                ichat.appendSibling(link);
        }

        // Append the rest of the message.
        String end = string.substring(lastEnd);
        if (ichat == null)
            ichat = new ChatComponentText(end);
        else if (end.length() > 0)
            ichat.appendText(string.substring(lastEnd));
        return ichat;
    }
}
