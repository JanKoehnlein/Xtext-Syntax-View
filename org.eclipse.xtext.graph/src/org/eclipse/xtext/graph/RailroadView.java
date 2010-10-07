package org.eclipse.xtext.graph;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.xtext.graph.actions.ExportToFileAction;
import org.eclipse.xtext.graph.actions.LinkWithEditorAction;
import org.eclipse.xtext.graph.actions.RailroadSelectionLinker;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * A railroad diagram view for Xtext grammars.
 * 
 * @author koehnlein
 */
@Singleton
public class RailroadView extends ViewPart {
	public static final String ID = "org.eclipse.xtext.graph.view";

	@Inject
	private RailroadSynchronizer synchronizer;

	@Inject
	private RailroadSelectionProvider selectionProvider;

	@Inject
	private ExportToFileAction exportAction;

	@Inject
	private LinkWithEditorAction linkWithEditorAction;

	@Inject
	private RailroadSelectionLinker selectionLinker;

	private IFigure rootFigure;

	private FigureCanvas canvas;

	private IFigure contents;

	public RailroadView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		canvas = new FigureCanvas(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		rootFigure = new Figure();
		rootFigure.addMouseListener(selectionProvider);
		rootFigure.setLayoutManager(new StackLayout());
		rootFigure.setVisible(true);
		canvas.setContents(rootFigure);
		getSite().setSelectionProvider(selectionProvider);
		createActions();
	}

	private void createActions() {
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		toolBarManager.add(exportAction);
		toolBarManager.add(linkWithEditorAction);
		selectionLinker.activate();
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		site.getWorkbenchWindow().getPartService().addPartListener(synchronizer);
	}

	@Override
	public void dispose() {
		selectionLinker.deactivate();
		getSite().getWorkbenchWindow().getPartService().addPartListener(synchronizer);
		super.dispose();
	}

	public void setContents(final IFigure newContents) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				if (contents != null)
					rootFigure.remove(contents);
				if (newContents != null)
					rootFigure.add(newContents);
				rootFigure.revalidate();
			}
		});
		this.contents = newContents;
		exportAction.setEnabled(newContents != null);
	}

	public IFigure getContents() {
		return contents;
	}

	public IFigure findFigureAt(Point location) {
		return rootFigure.findFigureAt(location);
	}

	public void reveal(IFigure figure) {
		Viewport viewport = canvas.getViewport();
		Rectangle viewportBounds = viewport.getBounds().getCopy();
		viewportBounds.translate(viewport.getViewLocation());
		Rectangle figureBounds = figure.getBounds().getCopy();
		figure.translateToAbsolute(figureBounds);
		figureBounds.translate(viewport.getViewLocation());
		if (!viewportBounds.contains(figureBounds))
			canvas.scrollSmoothTo(figureBounds.x, figureBounds.y);
	}

	@Override
	public void setFocus() {
		canvas.setFocus();
	}

	public Control getControl() {
		return canvas;
	}

}