package org.eclipse.xtext.graph;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.xtext.graph.figures.AbstractNode;
import org.eclipse.xtext.graph.figures.Diagram;
import org.eclipse.xtext.graph.figures.IGrammarElementReferer;
import org.eclipse.xtext.ui.editor.IURIEditorOpener;
import org.eclipse.xtext.ui.shared.Access;

public class RailroadView extends ViewPart {
	public static final String ID = "org.eclipse.xtext.graph.view";

	private ScrollPane scrollPane;

	private IURIEditorOpener uriEditorOpener;

	private RailroadSynchronizer synchronizer;

	public RailroadView() {
		uriEditorOpener = Access.getIURIEditorOpener().get();
		synchronizer = new RailroadSynchronizer(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		Canvas canvas = new Canvas(parent, SWT.NONE);
		LightweightSystem lightweightSystem = new LightweightSystem(canvas);
		scrollPane = new ScrollPane();
		scrollPane.setScrollBarVisibility(ScrollPane.AUTOMATIC);
		scrollPane.addMouseListener(new MouseListener() {

			@Override
			public void mousePressed(MouseEvent me) {
				Point location = me.getLocation();
				IFigure selectedFigure = scrollPane.findFigureAt(location);
				while (selectedFigure != null
						&& !(selectedFigure instanceof IGrammarElementReferer))
					selectedFigure = selectedFigure.getParent();
				if (selectedFigure != null) {
					IGrammarElementReferer element = (IGrammarElementReferer) selectedFigure;
					URI grammarElementURI = element.getGrammarElementURI();
					uriEditorOpener.open(grammarElementURI,
							!(selectedFigure instanceof Diagram));
					if(selectedFigure instanceof AbstractNode) 
						((AbstractNode) selectedFigure).setSelected(true);
				}
			}

			@Override
			public void mouseReleased(MouseEvent me) {
			}

			@Override
			public void mouseDoubleClicked(MouseEvent me) {
			}
		});
		lightweightSystem.setContents(scrollPane);
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		site.getWorkbenchWindow().getPartService()
				.addPartListener(synchronizer);
	}

	@Override
	public void dispose() {
		getSite().getWorkbenchWindow().getPartService()
				.addPartListener(synchronizer);
		super.dispose();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
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

}