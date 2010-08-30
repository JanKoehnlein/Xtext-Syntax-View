package org.eclipse.xtext.graph.figures;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

public abstract class AbstractNode extends CrossPoint implements IGrammarElementReferer {

	public static final int PADDING = 5;

	private Label label;
	private boolean isSelected = false;

	private URI grammarElementURI;

	protected AbstractNode(EObject grammarElement, String text, Font font) {
		if (grammarElement != null)
			grammarElementURI = EcoreUtil.getURI(grammarElement);
		setLayoutManager(new ToolbarLayout());
		setBackgroundColor(getUnselectedBackgroundColor());
		label = new Label(text);
		add(label);
		setBorder(createBorder());
		setFont(font);
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
		return ColorConstants.lightBlue;
	}

	protected Color getUnselectedBackgroundColor() {
		return ColorConstants.buttonLightest;
	}

	public URI getGrammarElementURI() {
		return grammarElementURI;
	}
}
