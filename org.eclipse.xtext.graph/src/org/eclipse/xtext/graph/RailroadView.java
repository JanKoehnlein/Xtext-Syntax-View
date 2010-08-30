package org.eclipse.xtext.graph;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.xtext.graph.figures.Diagram;

public class RailroadView extends ViewPart {
	public static final String ID = "org.eclipse.xtext.graph.view";

	private ScrollPane scrollPane;

	private RailroadSynchronizer synchronizer;

	private Canvas canvas;

	public RailroadView() {
		synchronizer = new RailroadSynchronizer(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		canvas = new Canvas(parent, SWT.NONE);
		LightweightSystem lightweightSystem = new LightweightSystem(canvas);
		scrollPane = new ScrollPane();
		scrollPane.setScrollBarVisibility(ScrollPane.AUTOMATIC);
		scrollPane.addMouseListener(new RailroadSelectionHelper(this));
		lightweightSystem.setContents(scrollPane);
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		site.getWorkbenchWindow().getPartService().addPartListener(synchronizer);
	}

	@Override
	public void dispose() {
		getSite().getWorkbenchWindow().getPartService().addPartListener(synchronizer);
		super.dispose();
	}

	public void setDiagram(final Diagram diagram) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				scrollPane.setContents(diagram);
				scrollPane.revalidate();
			}
		});
	}

	public IFigure findFigureAt(Point location) {
		return scrollPane.findFigureAt(location);
	}

	@Override
	public void setFocus() {
		canvas.setFocus();
	}

}