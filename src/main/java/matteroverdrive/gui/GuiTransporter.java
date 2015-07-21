/*
 * This file is part of Matter Overdrive
 * Copyright (c) 2015., Simeon Radivoev, All rights reserved.
 *
 * Matter Overdrive is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Matter Overdrive is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Matter Overdrive.  If not, see <http://www.gnu.org/licenses>.
 */

package matteroverdrive.gui;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.container.ContainerTransporter;
import matteroverdrive.gui.element.*;
import matteroverdrive.machines.transporter.TileEntityMachineTransporter;
import matteroverdrive.network.packet.server.PacketTransporterCommands;
import matteroverdrive.util.MOStringHelper;
import net.minecraft.entity.player.InventoryPlayer;

/**
 * Created by Simeon on 5/3/2015.
 */
public class GuiTransporter extends MOGuiMachine<TileEntityMachineTransporter>
{
    MOElementEnergy energy;
    ElementMatterStored matterStored;
    ElementIntegerField xCoords;
    ElementIntegerField yCoords;
    ElementIntegerField zCoords;
    MOElementButtonScaled saveButton;
    MOElementButtonScaled saveToNewButton;
    MOElementButtonScaled resetButton;
    MOElementTextField name;
    ElementTransportList list;
    MOElementButtonScaled removeLocation;

    String lastName = "";

    int xPos;
    int yPos;
    int zPos;

    public GuiTransporter(InventoryPlayer inventoryPlayer,TileEntityMachineTransporter machine)
    {
        super(new ContainerTransporter(inventoryPlayer,machine), machine,225,220);
        energy = new MOElementEnergy(this,xSize - 35,50,machine.getEnergyStorage());
        matterStored = new ElementMatterStored(this,xSize - 35,100,machine.getMatterStorage());

        xCoords = new ElementIntegerField(this,80,50,80,16);
        yCoords = new ElementIntegerField(this,80,50 + 18,80,16);
        zCoords = new ElementIntegerField(this,80,50 + 18 * 2,80,16);

        list = new ElementTransportList(this,this,45,30,140,100,machine);
        list.setName("Locations");

        name = new MOElementTextField(this,this,80 + 6,50 - 18,74,16);
        name.setTextOffset(6, 4);
        name.setBackground(MOElementButton.HOVER_TEXTURE_DARK);
        name.setName("LocationName");

        saveButton = new MOElementButtonScaled(this,this,70,50 + 18 * 3,"Save",50,18);
        saveButton.setNormalTexture(MOElementButton.NORMAL_TEXTURE);
        saveButton.setOverTexture(MOElementButton.HOVER_TEXTURE);
        saveButton.setText(MOStringHelper.translateToLocal("gui.label.button.save"));

        resetButton = new MOElementButtonScaled(this,this,70 + 52,50 + 18 * 3,"Reset",50,18);
        resetButton.setNormalTexture(MOElementButton.NORMAL_TEXTURE);
        resetButton.setOverTexture(MOElementButton.HOVER_TEXTURE);
        resetButton.setText(MOStringHelper.translateToLocal("gui.label.button.reset"));

        saveToNewButton = new MOElementButtonScaled(this,this,80,50 + 18 * 4,"New",80,18);
        saveToNewButton.setNormalTexture(MOElementButton.NORMAL_TEXTURE);
        saveToNewButton.setOverTexture(MOElementButton.HOVER_TEXTURE);
        saveToNewButton.setText(MOStringHelper.translateToLocal("gui.label.button.add"));

        removeLocation = new MOElementButtonScaled(this,this,50,ySize - 55,"Remove",60,18);
        removeLocation.setNormalTexture(MOElementButton.NORMAL_TEXTURE);

        removeLocation.setText(MOStringHelper.translateToLocal("gui.label.button.remove"));
    }

    @Override
    public void initGui()
    {
        super.initGui();

        pages.get(0).addElement(energy);
        pages.get(0).addElement(matterStored);
        pages.get(0).addElement(list);
        pages.get(1).addElement(xCoords);
        pages.get(1).addElement(yCoords);
        pages.get(1).addElement(zCoords);
        pages.get(1).addElement(saveButton);
        pages.get(1).addElement(saveToNewButton);
        pages.get(1).addElement(name);
        pages.get(1).addElement(resetButton);
        pages.get(0).addElement(removeLocation);

        pages.get(1).getElements().get(0).setPosition(120, 150);

        xCoords.init();
        yCoords.init();
        zCoords.init();

        updateInfo();

        AddHotbarPlayerSlots(inventorySlots, this);
    }

    private void updateInfo()
    {
        list.setSelectedIndex(machine.selectedLocation);

        name.setText(machine.getSelectedLocation().name);

        xCoords.setBounds(machine.xCoord - machine.getTransportRange(), machine.xCoord + machine.getTransportRange());
        yCoords.setBounds(machine.yCoord - machine.getTransportRange(), machine.yCoord + machine.getTransportRange());
        zCoords.setBounds(machine.zCoord - machine.getTransportRange(), machine.zCoord + machine.getTransportRange());

        updateCoordinates();

    }

    private void updateCoordinates()
    {
        xPos = machine.getSelectedLocation().x;
        yPos = machine.getSelectedLocation().y;
        zPos = machine.getSelectedLocation().z;

        xCoords.setNumber(xPos);
        yCoords.setNumber(yPos);
        zCoords.setNumber(zPos);

        energy.setEnergyRequired(-machine.getEnergyDrain());
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        super.drawGuiContainerForegroundLayer(x, y);

        if (pages.get(1).isVisible()) {
            getFontRenderer().drawString("X:", xCoords.getPosX() - 10, xCoords.getPosY() + 4, 0xFFFFFF);
            getFontRenderer().drawString(Integer.toString(xCoords.getNumber() - machine.xCoord), xCoords.getPosX() + xCoords.getWidth() + 4, xCoords.getPosY() + 4, 0xFFFFFF);

            getFontRenderer().drawString("Y:", yCoords.getPosX() - 10, yCoords.getPosY() + 4, 0xFFFFFF);
            getFontRenderer().drawString(Integer.toString(yCoords.getNumber() - machine.yCoord), yCoords.getPosX() + yCoords.getWidth() + 4, yCoords.getPosY() + 4, 0xFFFFFF);

            getFontRenderer().drawString("Z:", zCoords.getPosX() - 10, zCoords.getPosY() + 4, 0xFFFFFF);
            getFontRenderer().drawString(Integer.toString(zCoords.getNumber() - machine.zCoord), zCoords.getPosX() + zCoords.getWidth() + 4, zCoords.getPosY() + 4, 0xFFFFFF);
        }
    }

    @Override
    protected void updateElementInformation()
    {
        super.updateElementInformation();

        xPos = xCoords.getNumber();
        yPos = yCoords.getNumber();
        zPos = zCoords.getNumber();


    }

    @Override
    public void handleElementButtonClick(String buttonName, int mouseButton)
    {
        super.handleElementButtonClick(buttonName,mouseButton);

        if (buttonName == "Save")
        {
            machine.setSelectedLocation(xPos,yPos,zPos,name.getText());
            MatterOverdrive.packetPipeline.sendToServer(new PacketTransporterCommands(machine));
            updateInfo();
        }
        else if (buttonName == "New")
        {
            machine.addNewLocation(xPos,yPos,zPos,name.getText());
            MatterOverdrive.packetPipeline.sendToServer(new PacketTransporterCommands(machine));
            updateInfo();
        }
        else if (buttonName == "Reset")
        {
            machine.setSelectedLocation(machine.xCoord,machine.yCoord,machine.zCoord,machine.getSelectedLocation().name);
            MatterOverdrive.packetPipeline.sendToServer(new PacketTransporterCommands(machine));
            updateInfo();
        }
        else  if (buttonName == "Remove")
        {
            machine.removeLocation(list.getSelectedIndex());
            MatterOverdrive.packetPipeline.sendToServer(new PacketTransporterCommands(machine));
            updateInfo();
        }
    }

    public void onPageChange(int newPage)
    {
        if (newPage == 1) {
            updateInfo();
        }
    }

    @Override
    public  void ListSelectionChange(String name,int selected)
    {
        if (name == "Locations")
        {
            updateInfo();
            machine.selectedLocation = selected;
            MatterOverdrive.packetPipeline.sendToServer(new PacketTransporterCommands(machine));
            list.setSelectedIndex(machine.selectedLocation);
        }
    }
}
