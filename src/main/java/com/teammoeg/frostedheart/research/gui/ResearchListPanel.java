package com.teammoeg.frostedheart.research.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teammoeg.frostedheart.research.FHResearch;
import com.teammoeg.frostedheart.research.Research;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;

public class ResearchListPanel extends Panel {

    public static final int RESEARCH_HEIGHT = 18;
    public static final int RES_PANEL_WIDTH = 80;

    public ResearchScreen researchScreen;
    public PanelScrollBar scroll;
    public ResearchList rl;
    public ResearchListPanel(ResearchScreen panel) {
        super(panel);
        this.setOnlyInteractWithWidgetsInside(true);
        this.setOnlyRenderWidgetsInside(true);
        researchScreen = panel;
    }
    public static class ResearchList extends Panel{
    	public ResearchScreen researchScreen;
		public ResearchList(ResearchListPanel panel) {
			super(panel);
			researchScreen=panel.researchScreen;
			this.setWidth(99);
		}

		@Override
		public void addWidgets() {
	        int offset = 0;

	        for (Research r:FHResearch.getResearchesForRender(this.researchScreen.selectedCategory, true)) {
	            ResearchButton button = new ResearchButton(this, r);
	            add(button);
	            button.setPos(0,offset);
	            offset += 18;
	        }
	        this.setHeight(offset);
            researchScreen.researchListPanel.scroll.setMaxValue(offset+300);
		}

		@Override
		public void alignWidgets() {
		}
    	
    }
    public static class ResearchButton extends Button {

        Research research;
        ResearchList listPanel;

        public ResearchButton(ResearchList panel, Research research) {
            super(panel, research.getName(), ItemIcon.getItemIcon(research.getIcon()));
            this.research = research;
            this.listPanel =  panel;
            setSize(99, RESEARCH_HEIGHT);
        }

        @Override
        public void onClicked(MouseButton mouseButton) {
        	
            listPanel.researchScreen.selectResearch(research);
        }

        @Override
        public void draw(MatrixStack matrixStack, Theme theme, int x, int y, int w, int h) {
            //GuiHelper.setupDrawing();
			this.drawIcon(matrixStack, theme, x + 3, y + 1,16,16);
			if(research.isCompleted()) {
				 theme.drawString(matrixStack,research.getName(), x + 23, y + 6,Color4I.GREEN,0);
			}else if(!research.isUnlocked()) {
				theme.drawString(matrixStack,research.getName(), x + 23, y + 6,Color4I.RED,0);
			}else
            theme.drawString(matrixStack, research.getName(), x + 23, y +6,Color4I.rgb(0x474139),0);
			DrawDeskIcons.HLINE.draw(matrixStack,x, y+17, 99, 1);
        }
    }

    @Override
    public void addWidgets() {
    	rl=new ResearchList(this);
    	scroll=new TechScrollBar(this,rl);
    	add(rl);
    	add(scroll);
    	scroll.setX(100);
    	scroll.setSize(8,height);

    }

    @Override
    public void alignWidgets() {

    }

    @Override
	public void drawBackground(MatrixStack matrixStack, Theme theme, int x, int y, int w, int h) {
		//theme.drawPanelBackground(matrixStack, x, y, w, h);
	}

	@Override
    public void draw(MatrixStack matrixStack, Theme theme, int x, int y, int w, int h) {
        super.draw(matrixStack, theme, x, y, w, h);
    }
	@Override
	public boolean isEnabled() {
		return researchScreen.canEnable(this);
	}
}

