package org.eclipse.xtext.graph;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.xtext.graph.figures.Diagram;

public class RailroadView extends ViewPart {
	public static final String ID = "org.eclipse.xtext.graph.view";

	private ScrollPane scrollPane;

	private RailroadSynchronizer synchronizer;

	private Canvas canvas;

	private IAction exportAction;

	private Diagram diagram;

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
		createActions();
	}

	private void createActions() {
		exportAction = new Action("Export to file") {

			public static final int PADDING = 20;

			@Override
			public void run() {
				if (diagram != null) {
					FileDialog fileDialog = new FileDialog(getSite().getShell(), SWT.SAVE);
					fileDialog.setFilterExtensions(new String[] { "*.png" });
					fileDialog.setText("Choose diagram file");
					String fileName = fileDialog.open();
					Dimension preferredSize = diagram.getPreferredSize();
					Image image = new Image(Display.getDefault(), preferredSize.width + 2 * PADDING,
							preferredSize.height + 2 * PADDING);
					GC gc = new GC(image);
					SWTGraphics graphics = new SWTGraphics(gc);
					graphics.translate(PADDING, PADDING);
					diagram.paint(graphics);
					ImageData imageData = image.getImageData();
					ImageLoader imageLoader = new ImageLoader();
					imageLoader.data = new ImageData[] { imageData };
					imageLoader.save(fileName, SWT.IMAGE_PNG);
				}
			}
		};
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		exportAction.setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT));
		exportAction.setDisabledImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT_DISABLED));
		exportAction.setEnabled(false);
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		toolBarManager.add(exportAction);
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
		this.diagram = diagram;
		exportAction.setEnabled(diagram != null);
	}

	public IFigure findFigureAt(Point location) {
		return scrollPane.findFigureAt(location);
	}

	@Override
	public void setFocus() {
		canvas.setFocus();
	}

}