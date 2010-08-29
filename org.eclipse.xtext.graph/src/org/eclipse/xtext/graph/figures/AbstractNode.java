package org.eclipse.xtext.graph.figures;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.swt.graphics.Font;
import org.eclipse.xtext.AbstractElement;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.ParserRule;

public abstract class AbstractNode extends CrossPoint implements IGrammarElementReferer {

	public static final int INSETS = 5;

	private Label label;
	private boolean isSelected = false;

	private URI grammarElementURI;

	public static AbstractNode create(EObject grammarElement, String text) {
		if (grammarElement instanceof Keyword)
			return new RectangleNode(grammarElement, text);
		else if (grammarElement instanceof ParserRule)
			return new LabelNode(grammarElement, text);
		else if (grammarElement instanceof AbstractElement)
			return new RoundedNode(grammarElement, text);
		return null;
	}

	protected AbstractNode(EObject grammarElement, String text) {
		grammarElementURI = EcoreUtil.getURI(grammarElement);
		setLayoutManager(new ToolbarLayout());
		setBackgroundColor(ColorConstants.lightGray);
		label = new Label(text);
		add(label);
		setBorder(createBorder());
	}

	protected abstract Border createBorder();

	@Override
	public void setFont(Font f) {
		super.setFont(f);
		label.setFont(f);
	}

	public void setSelected(boolean selected) {
		if (selected != isSelected) {
			if (selected)
				setBackgroundColor(ColorConstants.lightBlue);
			else
				setBackgroundColor(ColorConstants.lightGray);
			isSelected = selected;
			invalidate();
		}
	}

	public URI getGrammarElementURI() {
		return grammarElementURI;
	}
}
