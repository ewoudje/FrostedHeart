package com.teammoeg.frostedheart.research.gui.tech;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teammoeg.frostedheart.research.FHResearch;
import com.teammoeg.frostedheart.research.Research;
import com.teammoeg.frostedheart.research.ResearchCategories;
import com.teammoeg.frostedheart.research.ResearchCategory;
import com.teammoeg.frostedheart.research.ResearchLevel;
import com.teammoeg.frostedheart.research.api.ClientResearchDataAPI;
import com.teammoeg.frostedheart.research.gui.TechIcons;

import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.input.Key;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;

public abstract class ResearchPanel extends Panel {

    public static final int PADDING = 2;
    public ResearchCategoryPanel researchCategoryPanel;
    public ResearchListPanel researchListPanel;
    public ResearchHierarchyPanel researchHierarchyPanel;
    public ResearchProgressPanel progressPanel;
    public ResearchCategory selectedCategory;
    public Research selectedResearch;
    public ResearchDetailPanel detailframe;
    public Panel modalPanel=null;
    public ResearchPanel(Panel p) {
    	super(p);
        researchCategoryPanel = new ResearchCategoryPanel(this);
        researchListPanel = new ResearchListPanel(this);
        researchHierarchyPanel = new ResearchHierarchyPanel(this);
        progressPanel = new ResearchProgressPanel(this);
        detailframe=new ResearchDetailPanel(this);
        //TODO default select on progress research
        Research cr=ClientResearchDataAPI.getData().getCurrentResearch().orElse(null);
        selectedCategory = cr==null?ResearchCategories.RESCUE:cr.getCategory();
        selectedResearch = cr==null?FHResearch.getFirstResearchInCategory(selectedCategory):cr;
    }

    @Override
    public void addWidgets() {
    	int sw=387;
    	int sh=203;
    	this.setSize(sw,sh);
        
    	researchCategoryPanel.setPosAndSize(165,0,190,21);
    	researchListPanel.setPosAndSize(12,74,114,118);
    	researchHierarchyPanel.setPosAndSize(160,23,210,160);
        progressPanel.setPosAndSize(14,19,111,51);
        detailframe.setPosAndSize((width-302)/2,(height-170)/2,302,170);
        add(researchCategoryPanel);
        add(researchListPanel);
        add(researchHierarchyPanel);
        add(progressPanel);
        add(detailframe);
    }


    public void selectCategory(@Nullable ResearchCategory category) {
        if (selectedCategory != category) {
            selectedCategory = category;
            if (FHResearch.getFirstResearchInCategory(category) != null)
				selectResearch(FHResearch.getFirstResearchInCategory(category));
            this.refreshWidgets();
        }
    }

    public void selectResearch(@Nullable Research research) {
        if (selectedResearch != research) {
            selectedResearch = research;
            this.refreshWidgets();
        }else {
        	detailframe.open(research);
        }
    }


    public static final int IN_PROGRESS_HEIGHT = 80;
    public static final int RESEARCH_LIST_WIDTH = 210;

    @Override
    public void drawBackground(MatrixStack matrixStack, Theme theme, int x, int y, int w, int h) {
        TechIcons.Background.draw(matrixStack, x, y, w, h);
    }

    @Override
    public void addMouseOverText(TooltipList list) {
        list.zOffset = 950;
        list.zOffsetItemTooltip = 500;
        super.addMouseOverText(list);
    }

	public void setModal(Panel p) {
		modalPanel=p;
	}
	public void closeModal(Panel p) {
		if(p==modalPanel)
		modalPanel=null;
	}
	public boolean canEnable(Panel p) {
		return modalPanel==null||modalPanel==p;
	}

	@Override
	public void drawWidget(MatrixStack arg0, Theme arg1, Widget arg2, int arg3, int arg4, int arg5, int arg6,
			int arg7) {
		GuiHelper.setupDrawing();
		super.drawWidget(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
	}


	@Override
	public boolean keyPressed(Key key) {
		if(key.esc()) {
			if(modalPanel!=null) {
				detailframe.close();
				return true;
			}
			this.onDisabled();
			//this.closeGui(true);
			return true;
		}
		return super.keyPressed(key);
	}
	public abstract void onDisabled();
	@Override
	public void alignWidgets() {
	}
	boolean enabled;

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void draw(MatrixStack arg0, Theme arg1, int arg2, int arg3, int arg4, int arg5) {
		if(enabled)
		super.draw(arg0, arg1, arg2, arg3, arg4, arg5);
	}


}
