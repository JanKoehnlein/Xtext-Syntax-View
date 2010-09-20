package org.eclipse.xtext.graph;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.xtext.graph.actions.ExportToFileAction;
import org.eclipse.xtext.graph.actions.LinkWithEditorAction;
import org.eclipse.xtext.graph.actions.RailroadSelectionLinker;
import org.eclipse.xtext.graph.figures.Diagram;

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

	private ScrollPane scrollPane;

	private Canvas canvas;

	private Diagram diagram;

	public RailroadView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		canvas = new Canvas(parent, SWT.NONE);
		LightweightSystem lightweightSystem = new LightweightSystem(canvas);
		scrollPane = new ScrollPane();
		scrollPane.setScrollBarVisibility(ScrollPane.AUTOMATIC);
		scrollPane.addMouseListener(selectionProvider);
		getSite().setSelectionProvider(selectionProvider);
		lightweightSystem.setContents(scrollPane);
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

	public void setDiagram(final Diagram diagram) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				scrollPane.setContents(diagram);
				scrollPane.revalidate();
			}
		});
		this.diagram = diagram;
		exportAction.setEnabled(diagram != null);
	}

	public Diagram getDiagram() {
		return diagram;
	}

	public IFigure findFigureAt(Point location) {
		return scrollPane.findFigureAt(location);
	}

	public void reveal(IFigure figure) {
		Rectangle rectangle = new Rectangle(scrollPane.getViewport().getBounds().getCopy()
				.translate(scrollPane.getViewport().getViewLocation()));
		if (rectangle.contains(figure.getBounds()))
			return;
		scrollPane.scrollTo(figure.getBounds().getLocation());
	}

	@Override
	public void setFocus() {
		canvas.setFocus();
	}
	
	public Control getControl() {
		return canvas;
	}

}