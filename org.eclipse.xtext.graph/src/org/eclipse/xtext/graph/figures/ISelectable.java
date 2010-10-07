package org.eclipse.xtext.graph.figures;

import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.text.Region;

public interface ISelectable extends IFigure {

	void setSelected(boolean isSelected);
	
	Region getTextRegion();
}
