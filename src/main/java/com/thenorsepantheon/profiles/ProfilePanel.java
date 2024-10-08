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
package com.thenorsepantheon.profiles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;
import net.runelite.http.api.worlds.WorldResult;

@Slf4j
class ProfilePanel extends JPanel
{
	private static final ImageIcon DELETE_ICON;
	private static final ImageIcon DELETE_HOVER_ICON;

	static
	{
		final BufferedImage deleteImg = ImageUtil.getResourceStreamFromClass(ProfilesPlugin.class, "delete_icon.png");
		DELETE_ICON = new ImageIcon(deleteImg);
		DELETE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(deleteImg, -100));
	}

	private final String loginText;

	ProfilePanel(final Client client, Profile profile, ProfilesConfig config, ProfilesPanel parent)
	{
		this.loginText = profile.getLogin();

		final ProfilePanel panel = this;

		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JPanel labelWrapper = new JPanel(new BorderLayout());
		labelWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		labelWrapper.setBorder(new CompoundBorder(
			BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR),
			BorderFactory.createLineBorder(ColorScheme.DARKER_GRAY_COLOR)
		));

		JPanel panelActions = new JPanel(new BorderLayout(3, 0));
		panelActions.setBorder(new EmptyBorder(0, 0, 0, 8));
		panelActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JLabel delete = new JLabel();
		delete.setIcon(DELETE_ICON);
		delete.setToolTipText("Delete account profile");
		delete.addMouseListener(new MouseAdapter()
		{
			@SneakyThrows
			@Override
			public void mousePressed(MouseEvent e)
			{
				panel.getParent().remove(panel);
				Profile.getProfiles().removeIf(p -> p.getLabel().equals(profile.getLabel())
					&& p.getLogin().equals(profile.getLogin()));
				ProfilesStorage.saveProfiles();
				parent.repaint();
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				delete.setIcon(DELETE_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				delete.setIcon(DELETE_ICON);
			}
		});

		panelActions.add(delete, BorderLayout.EAST);

		JLabel label = new JLabel();
		label.setText(formatLabel(profile, config.isStreamerMode()));
		label.setBorder(null);
		label.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		label.setPreferredSize(new Dimension(0, 24));
		label.setForeground(Color.WHITE);
		label.setBorder(new EmptyBorder(0, 8, 0, 0));

		labelWrapper.add(label, BorderLayout.CENTER);
		labelWrapper.add(panelActions, BorderLayout.EAST);

		JPanel bottomContainer = new JPanel(new BorderLayout());
		bottomContainer.setBorder(new EmptyBorder(8, 0, 8, 0));
		bottomContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		MouseAdapter clickAdapter = new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e) && client.getGameState() == GameState.LOGIN_SCREEN)
				{
					client.setUsername(loginText);
					if (profile.getWorld() != null)
					{
						int world = profile.getWorld();
						if (world > 300 && client.getWorld() != world)
						{
							client.changeWorld(ProfilesPlugin.findWorld(client, profile.getWorld()));
						}
					}
				}
				if (profile.getPassword() != null && !profile.isEncrypted())
				{
					client.setPassword(profile.getPassword());
				}
				else
				{
					client.setPassword("");
				}
			}
		};
		label.addMouseListener(clickAdapter);
		bottomContainer.addMouseListener(clickAdapter);

		JLabel login = new JLabel();
		login.setText(config.isStreamerMode() ? "Hidden email" : loginText);
		login.setBorder(null);
		login.setPreferredSize(new Dimension(0, 24));
		login.setForeground(Color.WHITE);
		login.setBorder(new EmptyBorder(0, 8, 0, 0));

		bottomContainer.add(login, BorderLayout.CENTER);

		add(labelWrapper, BorderLayout.NORTH);
		add(bottomContainer, BorderLayout.CENTER);
	}

	private String formatLabel(Profile profile, boolean hiddenWorld)
	{
		if (hiddenWorld || profile.getWorld() == null)
		{
			return profile.getLabel();
		}
		return profile.getLabel() + " (W" + profile.getWorld() + ")";
	}
}