/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package appeng.container.implementations;


import appeng.container.ContainerLocator;
import appeng.container.helper.PartContainerHelper;
import appeng.container.helper.PartOrTileContainerHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

import appeng.core.AEConfig;
import appeng.core.localization.PlayerMessages;
import appeng.helpers.WirelessTerminalGuiObject;
import appeng.util.Platform;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;


public class ContainerWirelessTerm extends ContainerMEPortableCell
{

	public static ContainerType<ContainerWirelessTerm> TYPE;

	private static final PartOrTileContainerHelper<ContainerWirelessTerm, WirelessTerminalGuiObject> helper
			= new PartOrTileContainerHelper<>(ContainerWirelessTerm::new, WirelessTerminalGuiObject.class);

	public static ContainerWirelessTerm fromNetwork(int windowId, PlayerInventory inv, PacketBuffer buf) {
		return helper.fromNetwork(windowId, inv, buf);
	}

	public static boolean open(PlayerEntity player, ContainerLocator locator) {
		return helper.open(player, locator);
	}

	private final WirelessTerminalGuiObject wirelessTerminalGUIObject;

	public ContainerWirelessTerm(int id, final PlayerInventory ip, final WirelessTerminalGuiObject gui )
	{
		super( TYPE, id, ip, gui );
		this.wirelessTerminalGUIObject = gui;
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

		if( !this.wirelessTerminalGUIObject.rangeCheck() )
		{
			if( Platform.isServer() && this.isValidContainer() )
			{
				this.getPlayerInv().player.sendMessage( PlayerMessages.OutOfRange.get() );
			}

			this.setValidContainer( false );
		}
		else
		{
			this.setPowerMultiplier( AEConfig.instance().wireless_getDrainRate( this.wirelessTerminalGUIObject.getRange() ) );
		}
	}
}
