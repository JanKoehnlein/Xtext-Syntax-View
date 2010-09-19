package org.eclipse.xtext.graph.actions;

import org.eclipse.draw2d.IFigure;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.xtext.graph.RailroadSelectionProvider;
import org.eclipse.xtext.graph.RailroadView;
import org.eclipse.xtext.graph.RailroadViewPreferences;
import org.eclipse.xtext.graph.figures.AbstractNode;
import org.eclipse.xtext.graph.figures.IGrammarElementReferer;
import org.eclipse.xtext.ui.editor.IURIEditorOpener;
import org.eclipse.xtext.ui.editor.XtextEditor;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class RailroadSelectionLinker implements IPropertyChangeListener {

	@Inject
	private IURIEditorOpener uriEditorOpener;

	@Inject
	private RailroadView view;

	@Inject
	private RailroadViewPreferences preferences;

	private AbstractNode currentSelectedNode;

	private ISelectionChangedListener diagramSelectionChangeListener = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			diagramSelectionChanged(event);
		}
	};

	private ISelectionChangedListener textSelectionChangeListener = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			textSelectionChanged(event);
		}
	};

	private IPostSelectionProvider currentTextSelectionProvider;

	public void activate() {
		view.getSite().getSelectionProvider().addSelectionChangedListener(diagramSelectionChangeListener);
		preferences.getPreferenceStore().addPropertyChangeListener(this);
		if (preferences.isLinkWithEditor())
			selectGrammarText(currentSelectedNode, false);
	}

	public void deactivate() {
		view.getSite().getSelectionProvider().removeSelectionChangedListener(diagramSelectionChangeListener);
		if (currentTextSelectionProvider != null)
			currentTextSelectionProvider.removePostSelectionChangedListener(textSelectionChangeListener);
		preferences.getPreferenceStore().removePropertyChangeListener(this);
	}

	protected void selectFigure(IFigure selectedFigure) {
		while (selectedFigure != null && !(selectedFigure instanceof IGrammarElementReferer))
			selectedFigure = selectedFigure.getParent();
		if (selectedFigure != null) {
			if (currentSelectedNode != null)
				currentSelectedNode.setSelected(false);
			if (selectedFigure instanceof AbstractNode) {
				((AbstractNode) selectedFigure).setSelected(true);
				currentSelectedNode = (AbstractNode) selectedFigure;
			} else {
				currentSelectedNode = null;
			}
		}
	}

	protected void selectGrammarText(IFigure selectedFigure, boolean isActivateEditor) {
		if (selectedFigure != null) {
			IGrammarElementReferer element = (IGrammarElementReferer) selectedFigure;
			final URI grammarElementURI = element.getGrammarElementURI();
			if (grammarElementURI != null) {
				final boolean isSelectElement = selectedFigure instanceof AbstractNode;
				// enqueue to make sure the diagram is updated before
				if (isActivateEditor && isSelectElement) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							uriEditorOpener.open(grammarElementURI, isSelectElement);
						}
					});
				} else {
					uriEditorOpener.open(grammarElementURI, isSelectElement);
					view.getSite().getPage().activate(view);
				}
			}
		}
	}

	public void diagramSelectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			if (structuredSelection.size() == 1) {
				Object selectedElement = structuredSelection.getFirstElement();
				if (selectedElement instanceof IFigure) {
					IFigure selectedFigure = (IFigure) selectedElement;
					selectFigure(selectedFigure);
					boolean isDoubleClick = event instanceof RailroadSelectionProvider.DoubleClickEvent;
					if (isDoubleClick || preferences.isLinkWithEditor())
						selectGrammarText(selectedFigure, isDoubleClick);
				}
			}
		}
	}

	public void textSelectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		if (selection instanceof ITextSelection && !selection.isEmpty()) {
			ITextSelection textSelection = (ITextSelection) selection;
			int offset = textSelection.getOffset();
			for (Object child : view.getDiagram().getChildren()) {
				if (child instanceof AbstractNode) {
					AbstractNode childNode = (AbstractNode) child;
					Region textRegion = childNode.getTextRegion();
					if (textRegion != null && textRegion.getOffset() <= offset
							&& textRegion.getOffset() + textRegion.getLength() >= offset) {
						selectFigure(childNode);
						view.reveal(childNode);
					}
				}
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(RailroadViewPreferences.LINK_WITH_EDITOR_KEY)
				&& event.getNewValue() == Boolean.TRUE) {
			if (currentSelectedNode != null)
				selectGrammarText(currentSelectedNode, true);
		}
	}

	public void setXtextEditor(XtextEditor xtextEditor) {
		if (currentTextSelectionProvider != null) {
			currentTextSelectionProvider.removePostSelectionChangedListener(textSelectionChangeListener);
		}
		currentTextSelectionProvider = (IPostSelectionProvider) xtextEditor.getSelectionProvider();
		currentTextSelectionProvider.addPostSelectionChangedListener(textSelectionChangeListener);
	}

}
