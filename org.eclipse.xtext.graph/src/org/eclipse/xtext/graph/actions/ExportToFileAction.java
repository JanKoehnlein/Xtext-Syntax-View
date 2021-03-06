package org.eclipse.xtext.graph.actions;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.graph.RailroadView;

import com.google.inject.Inject;

/**
 * Exports an Xtext grammar railroad diagram to an image file.
 * 
 * @author koehnlein
 */
public class ExportToFileAction extends Action {

	@Inject
	private RailroadView railroadView;

	public static final int PADDING = 20;

	public ExportToFileAction() {
		setText("Export to file");
		setDescription("Exports this diagram to an image file");
		setToolTipText("Exports this diagram to an image file");
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT_DISABLED));
	}

	@Override
	public void run() {
		IFigure contents = railroadView.getContents();
		if (contents != null) {
			FileDialog fileDialog = new FileDialog(this.railroadView.getSite().getShell(), SWT.SAVE);
			fileDialog.setFilterExtensions(new String[] { "*.png" });
			fileDialog.setText("Choose diagram file");
			String fileName = fileDialog.open();
			Dimension preferredSize = contents.getPreferredSize();
			Image image = new Image(Display.getDefault(), preferredSize.width + 2 * PADDING, preferredSize.height + 2
					* PADDING);
			GC gc = new GC(image);
			SWTGraphics graphics = new SWTGraphics(gc);
			graphics.translate(PADDING, PADDING);
			graphics.translate(contents.getBounds().getLocation().getNegated());
			contents.paint(graphics);
			ImageData imageData = image.getImageData();
			ImageLoader imageLoader = new ImageLoader();
			imageLoader.data = new ImageData[] { imageData };
			imageLoader.save(fileName, SWT.IMAGE_PNG);
		}
	}
}