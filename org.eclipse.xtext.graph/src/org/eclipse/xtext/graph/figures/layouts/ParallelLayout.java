package org.eclipse.xtext.graph.figures.layouts;

import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.xtext.graph.figures.ISegmentFigure;
import org.eclipse.xtext.graph.figures.ILayoutConstants;

/**
 * Layouts children vertically with common entry and exit nodes to the left / right.
 * 
 * @author koehnlein
 */
public class ParallelLayout extends AbstractLayout {

	private int hmargin;

	public ParallelLayout(int hmargin) {
		this.hmargin = hmargin;
	}
	
	public ParallelLayout() {
		this(0);
	}
	
	@Override
	public void layout(IFigure container) {
		if (container instanceof ISegmentFigure) {
			ISegmentFigure containerSegment = (ISegmentFigure) container;
			int width = 0;
			for (Object child : containerSegment.getChildren()) {
				if (child instanceof ISegmentFigure) {
					Dimension childSize = ((ISegmentFigure) child).getPreferredSize();
					width = Math.max(width, childSize.width);
				}
			}
			int y = 0;
			Rectangle bounds = Rectangle.SINGLETON;
			for (Object child : containerSegment.getChildren()) {
				if (child instanceof ISegmentFigure) {
					Dimension childSize = ((ISegmentFigure) child).getPreferredSize();
					bounds.setLocation(ILayoutConstants.PARALLEL_SEGMENT_HSPACE + hmargin +(width - childSize.width) / 2, y);
					bounds.setSize(childSize);
					((ISegmentFigure) child).setBounds(bounds);
					y += childSize.height + ILayoutConstants.VSPACE;
				}
			}
			y-=ILayoutConstants.VSPACE;
			bounds.setLocation(hmargin, y / 2);
			bounds.setSize(0, 0);
			containerSegment.getEntry().setBounds(bounds);
			bounds.setLocation(width + 2* ILayoutConstants.PARALLEL_SEGMENT_HSPACE + hmargin, y / 2);
			containerSegment.getExit().setBounds(bounds);
		}
	}

	@Override
	protected Dimension calculatePreferredSize(IFigure container, int wHint, int hHint) {
		if (container instanceof ISegmentFigure) {
			ISegmentFigure containerSegment = (ISegmentFigure) container;
			int width = 0;
			int height = 0;
			for (Object child : containerSegment.getChildren()) {
				if (child instanceof ISegmentFigure) {
					Dimension childSize = ((ISegmentFigure) child).getPreferredSize();
					width = Math.max(width, childSize.width);
					height += childSize.height + ILayoutConstants.VSPACE;
				}
			}
			height -=ILayoutConstants.VSPACE;
			width +=  2*ILayoutConstants.PARALLEL_SEGMENT_HSPACE + 2*hmargin +1;
			return new Dimension(width, height);
		}
		return new Dimension();
	}

}