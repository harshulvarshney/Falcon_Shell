package com.ops.cli.util;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

/**
 * @author harshul.varshney
 */
@Component
public class CliPromptProvider implements PromptProvider {

    @Override
    public AttributedString getPrompt() {
        return new AttributedString("FALCON:>",
                AttributedStyle.BOLD.foreground(AttributedStyle.YELLOW)
        );
    }

}
