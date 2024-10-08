/*
 * Copyright (c) 2020, Spedwards <https://github.com/Spedwards>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.thenorsepantheon.profiles.ui;

import static com.thenorsepantheon.profiles.ui.Fields.PREFERRED_SIZE;
import static com.thenorsepantheon.profiles.ui.Fields.MINIMUM_SIZE;
import static com.thenorsepantheon.profiles.ui.Fields.FOREGROUND_COLOUR;
import static com.thenorsepantheon.profiles.ui.Fields.BACKGROUND_COLOUR;
import static com.thenorsepantheon.profiles.ui.Fields.ACTIVE_COLOUR;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JPasswordField;
import lombok.Setter;

public class PasswordField extends JPasswordField
{
	private final PasswordField self;

	private final String placeholder;
	@Setter
	private boolean obfuscate;

	public PasswordField(String placeholder)
	{
		this(placeholder, true);
	}

	public PasswordField(String placeholder, boolean obfuscate)
	{
		super(placeholder);

		this.self = this;

		this.placeholder = placeholder;
		this.obfuscate = obfuscate;

		this.setEchoChar((char) 0);
		this.setPreferredSize(PREFERRED_SIZE);
		this.setMinimumSize(MINIMUM_SIZE);
		this.setForeground(FOREGROUND_COLOUR);
		this.setBackground(BACKGROUND_COLOUR);

		this.addFocusListener(new FocusListener()
		{
			@Override
			public void focusGained(FocusEvent focusEvent)
			{
				if (self.placeholder.equals(String.valueOf(self.getPassword())))
				{
					self.setText("");
					self.setForeground(ACTIVE_COLOUR);
					if (self.obfuscate)
					{
						self.setEchoChar('*');
					}
				}
			}

			@Override
			public void focusLost(FocusEvent focusEvent)
			{
				if (self.getPassword().length == 0)
				{
					self.setText(self.placeholder);
					self.setEchoChar((char) 0);
					self.setForeground(FOREGROUND_COLOUR);
				}
			}
		});

		this.addMouseListener(Fields.getHoverAdapter(this));
	}

	public void resetState()
	{
		this.setText(this.placeholder);
		this.setEchoChar((char) 0);
		this.setForeground(FOREGROUND_COLOUR);
	}

}
