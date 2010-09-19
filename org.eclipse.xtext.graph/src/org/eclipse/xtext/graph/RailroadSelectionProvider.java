package org.eclipse.xtext.graph;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.xtext.graph.figures.AbstractNode;
import org.eclipse.xtext.graph.figures.IGrammarElementReferer;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Manages selection and navigation in the railroad diagram view.
 * 
 * @author koehnlein
 */
@Singleton
public class RailroadSelectionProvider implements MouseListener, ISelectionProvider {

	public static class DoubleClickEvent extends SelectionChangedEvent {
		private static final long serialVersionUID = 6328075027502345297L;

		public DoubleClickEvent(ISelectionProvider source, ISelection selection) {
			super(source, selection);
		}
	}

	@Inject
	private RailroadView view;

	private ListenerList selectionListeners = new ListenerList();

	private IFigure currentSelectedFigure;

	@Override
	public void mousePressed(MouseEvent me) {
		setSelection(me, false);
	}

	@Override
	public void mouseReleased(MouseEvent me) {
		// do nothing
	}

	@Override
	public void mouseDoubleClicked(MouseEvent me) {
		setSelection(me, true);
	}

	protected void setSelection(MouseEvent me, boolean isDoubleClick) {
		IFigure selectedFigure = view.findFigureAt(me.getLocation());
		while (selectedFigure != null && !(selectedFigure instanceof IGrammarElementReferer))
			selectedFigure = selectedFigure.getParent();
		if (selectedFigure != null) {
			setSelection(new StructuredSelection(selectedFigure), isDoubleClick);
		}
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionListeners.add(listener);
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionListeners.remove(listener);
	}

	@Override
	public ISelection getSelection() {
		if(currentSelectedFigure != null) 
			return new StructuredSelection(currentSelectedFigure);
		else 
			return StructuredSelection.EMPTY;
	}

	protected void setSelection(ISelection selection, boolean isDoubleClick) {
		IFigure selectedFigure = getSelectedFigure(selection);
		if(currentSelectedFigure != null && currentSelectedFigure instanceof AbstractNode) {
			((AbstractNode)currentSelectedFigure).setSelected(false);
		}
		currentSelectedFigure = selectedFigure;
		if (selectedFigure instanceof AbstractNode) {
			((AbstractNode) selectedFigure).setSelected(true);
		}
		SelectionChangedEvent event = isDoubleClick ? new DoubleClickEvent(this, selection)
					: new SelectionChangedEvent(this, selection);
		for (Object listener : selectionListeners.getListeners())
			((ISelectionChangedListener) listener).selectionChanged(event);
	}

	@Override
	public void setSelection(ISelection selection) {
		setSelection(selection, false);
	}

	protected IFigure getSelectedFigure(ISelection selection) {
		if (selection instanceof IStructuredSelection && ((IStructuredSelection) selection).size() == 1) {
			Object selectedElement = ((IStructuredSelection) selection).getFirstElement();
			if (selectedElement instanceof IFigure)
				return (IFigure) selectedElement;
		}
		return null;
	}
}
