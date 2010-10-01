package org.eclipse.xtext.graph.figures.primitives;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.xtext.graph.figures.ILayoutConstants;
import org.eclipse.xtext.parsetree.CompositeNode;
import org.eclipse.xtext.parsetree.NodeUtil;

/**
 * Base class of all nodes.
 * 
 * @author koehnlein
 */
public abstract class AbstractNode extends CrossPoint implements IGrammarElementReferer {

	public static final int PADDING = 5;

	private Label label;
	private boolean isSelected = false;

	private URI grammarElementURI;

	private Region textRegion;

	protected AbstractNode(EObject grammarElement, String text, Font font) {
		if (grammarElement != null)
			grammarElementURI = EcoreUtil.getURI(grammarElement);
		setLayoutManager(new ToolbarLayout());
		setBackgroundColor(getUnselectedBackgroundColor());
		label = new Label(text);
		add(label);
		setBorder(createBorder());
		setFont(font);
		CompositeNode node = NodeUtil.getNode(grammarElement);
		if (node != null)
			textRegion = new Region(node.getOffset(), node.getLength());
	}

	protected abstract Border createBorder();

	@Override
	public void setFont(Font f) {
		super.setFont(f);
		label.setFont(f);
	}

	public void setSelected(boolean isSelected) {
		if (isSelected != this.isSelected) {
			if (isSelected)
				setBackgroundColor(getSelectedBackgroundColor());
			else
				setBackgroundColor(getUnselectedBackgroundColor());
			this.isSelected = isSelected;
			invalidate();
		}
	}

	protected Color getSelectedBackgroundColor() {
		return ILayoutConstants.NODE_SELECTION_COLOR;
	}

	protected Color getUnselectedBackgroundColor() {
		return ColorConstants.buttonLightest;
	}

	public URI getGrammarElementURI() {
		return grammarElementURI;
	}

	public Region getTextRegion() {
		return textRegion;
	}

	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}
}
