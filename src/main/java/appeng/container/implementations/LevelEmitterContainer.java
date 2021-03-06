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

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;

import appeng.api.config.FuzzyMode;
import appeng.api.config.LevelType;
import appeng.api.config.RedstoneMode;
import appeng.api.config.SecurityPermissions;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.container.ContainerLocator;
import appeng.container.guisync.GuiSync;
import appeng.container.slot.FakeTypeOnlySlot;
import appeng.container.slot.RestrictedInputSlot;
import appeng.parts.automation.LevelEmitterPart;
import appeng.util.Platform;

public class LevelEmitterContainer extends UpgradeableContainer {

    public static ContainerType<LevelEmitterContainer> TYPE;

    private static final ContainerHelper<LevelEmitterContainer, LevelEmitterPart> helper = new ContainerHelper<>(
            LevelEmitterContainer::new, LevelEmitterPart.class, SecurityPermissions.BUILD);

    public static LevelEmitterContainer fromNetwork(int windowId, PlayerInventory inv, PacketBuffer buf) {
        return helper.fromNetwork(windowId, inv, buf);
    }

    public static boolean open(PlayerEntity player, ContainerLocator locator) {
        return helper.open(player, locator);
    }

    private final LevelEmitterPart lvlEmitter;

    @OnlyIn(Dist.CLIENT)
    private TextFieldWidget textField;
    @GuiSync(2)
    public LevelType lvType;
    @GuiSync(3)
    public long EmitterValue = -1;
    @GuiSync(4)
    public YesNo cmType;

    public LevelEmitterContainer(int id, final PlayerInventory ip, final LevelEmitterPart te) {
        super(TYPE, id, ip, te);
        this.lvlEmitter = te;
    }

    @OnlyIn(Dist.CLIENT)
    public void setTextField(final TextFieldWidget level) {
        this.textField = level;
        this.textField.setText(String.valueOf(this.EmitterValue));
    }

    public void setLevel(final long l, final PlayerEntity player) {
        this.lvlEmitter.setReportingValue(l);
        this.EmitterValue = l;
    }

    @Override
    protected void setupConfig() {
        final IItemHandler upgrades = this.getUpgradeable().getInventoryByName("upgrades");
        if (this.availableUpgrades() > 0) {
            this.addSlot((new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.UPGRADES, upgrades, 0, 187, 8,
                    this.getPlayerInventory())).setNotDraggable());
        }
        if (this.availableUpgrades() > 1) {
            this.addSlot((new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.UPGRADES, upgrades, 1, 187,
                    8 + 18, this.getPlayerInventory())).setNotDraggable());
        }
        if (this.availableUpgrades() > 2) {
            this.addSlot((new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.UPGRADES, upgrades, 2, 187,
                    8 + 18 * 2, this.getPlayerInventory())).setNotDraggable());
        }
        if (this.availableUpgrades() > 3) {
            this.addSlot((new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.UPGRADES, upgrades, 3, 187,
                    8 + 18 * 3, this.getPlayerInventory())).setNotDraggable());
        }

        final IItemHandler inv = this.getUpgradeable().getInventoryByName("config");
        final int y = 40;
        final int x = 80 + 44;
        this.addSlot(new FakeTypeOnlySlot(inv, 0, x, y));
    }

    @Override
    protected boolean supportCapacity() {
        return false;
    }

    @Override
    public int availableUpgrades() {

        return 1;
    }

    @Override
    public void detectAndSendChanges() {
        this.verifyPermissions(SecurityPermissions.BUILD, false);

        if (Platform.isServer()) {
            this.EmitterValue = this.lvlEmitter.getReportingValue();
            this.setCraftingMode(
                    (YesNo) this.getUpgradeable().getConfigManager().getSetting(Settings.CRAFT_VIA_REDSTONE));
            this.setLevelMode((LevelType) this.getUpgradeable().getConfigManager().getSetting(Settings.LEVEL_TYPE));
            this.setFuzzyMode((FuzzyMode) this.getUpgradeable().getConfigManager().getSetting(Settings.FUZZY_MODE));
            this.setRedStoneMode(
                    (RedstoneMode) this.getUpgradeable().getConfigManager().getSetting(Settings.REDSTONE_EMITTER));
        }

        this.standardDetectAndSendChanges();
    }

    @Override
    public void onUpdate(final String field, final Object oldValue, final Object newValue) {
        if (field.equals("EmitterValue")) {
            if (this.textField != null) {
                this.textField.setText(String.valueOf(this.EmitterValue));
            }
        }
    }

    @Override
    public YesNo getCraftingMode() {
        return this.cmType;
    }

    @Override
    public void setCraftingMode(final YesNo cmType) {
        this.cmType = cmType;
    }

    public LevelType getLevelMode() {
        return this.lvType;
    }

    private void setLevelMode(final LevelType lvType) {
        this.lvType = lvType;
    }
}
