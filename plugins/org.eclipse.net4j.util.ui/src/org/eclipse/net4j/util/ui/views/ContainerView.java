/*
 * Copyright (c) 2007-2013, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.views;

import org.eclipse.net4j.ui.shared.SharedIcons;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.internal.ui.actions.IntrospectAction;
import org.eclipse.net4j.util.internal.ui.bundle.OM;
import org.eclipse.net4j.util.internal.ui.messages.Messages;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.actions.SafeAction;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;

public abstract class ContainerView extends ViewPart implements ISelectionProvider, ISetSelectionTarget
{
  private Shell shell;

  private ContainerItemProvider<IContainer<Object>> itemProvider;

  private TreeViewer viewer;

  private ISelectionChangedListener selectionListener = new ISelectionChangedListener()
  {
    public void selectionChanged(SelectionChangedEvent event)
    {
      ITreeSelection selection = (ITreeSelection)event.getSelection();
      IActionBars bars = getViewSite().getActionBars();
      ContainerView.this.selectionChanged(bars, selection);
    }
  };

  private Action refreshAction = new RefreshAction();

  private Action collapseAllAction = new CollapseAllAction();

  private PropertySheetPage propertySheetPage;

  public ContainerView()
  {
  }

  public Shell getShell()
  {
    return shell;
  }

  public TreeViewer getViewer()
  {
    return viewer;
  }

  @Override
  public void setFocus()
  {
    viewer.getControl().setFocus();
  }

  public void resetInput()
  {
    final IContainer<?> container = getContainer();

    Runnable runnable = new Runnable()
    {
      public void run()
      {
        try
        {
          viewer.setInput(container);
        }
        catch (RuntimeException ignore)
        {
        }
      }
    };

    try
    {
      Display display = getDisplay();
      if (display.getThread() == Thread.currentThread())
      {
        runnable.run();
      }
      else
      {
        display.asyncExec(runnable);
      }
    }
    catch (RuntimeException ignore)
    {
    }
  }

  /**
   * @since 3.0
   */
  public ISelection getSelection()
  {
    if (viewer != null)
    {
      return viewer.getSelection();
    }

    return StructuredSelection.EMPTY;
  }

  /**
   * @since 3.0
   */
  public void setSelection(ISelection selection)
  {
    if (viewer != null)
    {
      viewer.setSelection(selection);
    }
  }

  /**
   * @since 3.0
   */
  public void addSelectionChangedListener(ISelectionChangedListener listener)
  {
    if (viewer != null)
    {
      viewer.addSelectionChangedListener(listener);
    }
  }

  /**
   * @since 3.0
   */
  public void removeSelectionChangedListener(ISelectionChangedListener listener)
  {
    if (viewer != null)
    {
      viewer.removeSelectionChangedListener(listener);
    }
  }

  public void selectReveal(ISelection selection)
  {
    if (viewer != null)
    {
      viewer.setSelection(selection, true);
    }
  }

  @Override
  public final void createPartControl(Composite parent)
  {
    shell = parent.getShell();
    Composite composite = UIUtil.createGridComposite(parent, 1);

    Control control = createUI(composite);
    control.setLayoutData(UIUtil.createGridData());

    hookContextMenu();
    hookDoubleClick();
    contributeToActionBars();
    createdUI();
  }

  protected Control createUI(Composite parent)
  {
    viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    initViewer();

    viewer.addSelectionChangedListener(selectionListener);
    getSite().setSelectionProvider(this);
    return viewer.getControl();
  }

  /**
   * @since 3.1
   */
  protected void createdUI()
  {
  }

  /**
   * @since 3.1
   */
  @SuppressWarnings("deprecation")
  protected void initViewer()
  {
    itemProvider = createContainerItemProvider();
    viewer.setContentProvider(createContentProvider());
    viewer.setLabelProvider(createLabelProvider());
    viewer.setSorter(createViewerSorter());
    resetInput();
  }

  /**
   * @since 3.3
   */
  @SuppressWarnings("deprecation")
  protected org.eclipse.jface.viewers.ViewerSorter createViewerSorter()
  {
    return itemProvider;
  }

  /**
   * @since 3.0
   */
  protected IContentProvider createContentProvider()
  {
    return itemProvider;
  }

  /**
   * @since 3.0
   */
  protected IBaseLabelProvider createLabelProvider()
  {
    ILabelDecorator labelDecorator = createLabelDecorator();
    return new DecoratingStyledCellLabelProvider(itemProvider, labelDecorator, null);
  }

  /**
   * @since 3.0
   */
  protected ILabelDecorator createLabelDecorator()
  {
    return PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
  }

  protected ContainerItemProvider<IContainer<Object>> createContainerItemProvider()
  {
    return new ContainerItemProvider<IContainer<Object>>(getRootElementFilter())
    {
      @Override
      public Image getImage(Object obj)
      {
        Image image = getElementImage(obj);
        if (image == null)
        {
          image = super.getImage(obj);
        }

        return image;
      }

      @Override
      public String getText(Object obj)
      {
        String text = getElementText(obj);
        if (text == null)
        {
          text = super.getText(obj);
        }

        return text;
      }

      @Override
      public Color getForeground(Object obj)
      {
        Color color = getElementForeground(obj);
        if (color == null)
        {
          color = super.getForeground(obj);
        }

        return color;
      }

      @Override
      public Color getBackground(Object obj)
      {
        Color color = getElementBackground(obj);
        if (color == null)
        {
          color = super.getBackground(obj);
        }

        return color;
      }

      @Override
      public Font getFont(Object obj)
      {
        Font font = getElementFont(obj);
        if (font == null)
        {
          font = super.getFont(obj);
        }

        return font;
      }
    };
  }

  protected String getElementText(Object element)
  {
    return null;
  }

  protected Image getElementImage(Object element)
  {
    return null;
  }

  /**
   * @since 3.0
   */
  protected Color getElementForeground(Object element)
  {
    return null;
  }

  /**
   * @since 3.0
   */
  protected Color getElementBackground(Object element)
  {
    return null;
  }

  /**
   * @since 3.0
   */
  protected Font getElementFont(Object element)
  {
    return null;
  }

  protected IElementFilter getRootElementFilter()
  {
    return null;
  }

  protected abstract IContainer<?> getContainer();

  protected void hookDoubleClick()
  {
    viewer.addDoubleClickListener(new IDoubleClickListener()
    {
      public void doubleClick(DoubleClickEvent event)
      {
        ITreeSelection selection = (ITreeSelection)viewer.getSelection();
        Object object = selection.getFirstElement();
        doubleClicked(object);
      }
    });
  }

  protected void hookContextMenu()
  {
    MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener()
    {
      public void menuAboutToShow(IMenuManager manager)
      {
        ITreeSelection selection = (ITreeSelection)viewer.getSelection();
        fillContextMenu(manager, selection);
      }
    });

    Menu menu = menuMgr.createContextMenu(viewer.getControl());
    viewer.getControl().setMenu(menu);
    getSite().registerContextMenu(menuMgr, viewer);
  }

  protected void contributeToActionBars()
  {
    IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }

  protected void fillLocalPullDown(IMenuManager manager)
  {
    addRefreshAction(manager);
    manager.add(new IntrospectAction(getViewer()));
  }

  protected void fillLocalToolBar(IToolBarManager manager)
  {
    addCollapseAllAction(manager);
  }

  protected void fillContextMenu(IMenuManager manager, ITreeSelection selection)
  {
    itemProvider.fillContextMenu(manager, selection);
  }

  /**
   * @since 3.5
   */
  protected void addSeparator(IContributionManager manager)
  {
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
  }

  /**
   * @since 3.5
   */
  protected void addRefreshAction(IContributionManager manager)
  {
    manager.add(refreshAction);
  }

  /**
   * @since 3.5
   */
  protected void addCollapseAllAction(IContributionManager manager)
  {
    manager.add(collapseAllAction);
  }

  protected void selectionChanged(IActionBars bars, ITreeSelection selection)
  {
  }

  protected void doubleClicked(Object object)
  {
    if (object instanceof ContainerItemProvider.ErrorElement)
    {
      try
      {
        UIUtil.getActiveWorkbenchPage().showView(UIUtil.ERROR_LOG_ID);
      }
      catch (PartInitException ex)
      {
        OM.LOG.error(ex);
      }
    }
    else if (object != null && viewer.isExpandable(object))
    {
      if (viewer.getExpandedState(object))
      {
        viewer.collapseToLevel(object, TreeViewer.ALL_LEVELS);
      }
      else
      {
        viewer.expandToLevel(object, 1);
      }
    }
  }

  /**
   * @since 3.1
   */
  protected void refreshPressed()
  {
    itemProvider.clearNodesCache();
    UIUtil.refreshViewer(viewer);
  }

  /**
   * @since 3.3
   */
  protected void collapseAllPressed()
  {
    getViewer().collapseAll();
  }

  protected void closeView()
  {
    try
    {
      getDisplay().syncExec(new Runnable()
      {
        public void run()
        {
          try
          {
            getSite().getPage().hideView(ContainerView.this);
            ContainerView.this.dispose();
          }
          catch (Exception ignore)
          {
          }
        }
      });
    }
    catch (Exception ignore)
    {
    }
  }

  protected void showMessage(String message)
  {
    showMessage(MessageType.INFORMATION, message);
  }

  protected boolean showMessage(MessageType type, String message)
  {
    switch (type)
    {
    case INFORMATION:
      MessageDialog.openInformation(viewer.getControl().getShell(), getTitle(), message);
      return true;

    case ERROR:
      MessageDialog.openError(viewer.getControl().getShell(), getTitle(), message);
      return true;

    case WARNING:
      MessageDialog.openWarning(viewer.getControl().getShell(), getTitle(), message);
      return true;

    case CONFIRM:
      return MessageDialog.openConfirm(viewer.getControl().getShell(), getTitle(), message);

    case QUESTION:
      return MessageDialog.openQuestion(viewer.getControl().getShell(), getTitle(), message);

    default:
      return true;
    }
  }

  /**
   * @since 3.1
   */
  protected Action getRefreshAction()
  {
    return refreshAction;
  }

  /**
   * @since 3.3
   */
  public Action getCollapseAllAction()
  {
    return collapseAllAction;
  }

  protected Display getDisplay()
  {
    Display display = viewer.getControl().getDisplay();
    if (display == null)
    {
      display = UIUtil.getDisplay();
    }

    return display;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T getAdapter(Class<T> adapter)
  {
    if (adapter == IPropertySheetPage.class)
    {
      if (propertySheetPage == null)
      {
        propertySheetPage = new PropertySheetPage();
      }

      return (T)propertySheetPage;
    }

    return super.getAdapter(adapter);
  }

  /**
   * @since 3.5
   */
  public void refreshPropertySheetPage()
  {
    if (propertySheetPage != null)
    {
      propertySheetPage.refresh();
    }
  }

  public void refreshViewer(boolean updateLabels)
  {
    itemProvider.refreshViewer(updateLabels);
  }

  public void refreshElement(Object element, boolean updateLabels)
  {
    itemProvider.refreshElement(element, updateLabels);
  }

  public void updateLabels(Object element)
  {
    itemProvider.updateLabels(element);
  }

  public void revealElement(Object element)
  {
    itemProvider.revealElement(element);
  }

  /**
   * @since 3.3
   */
  public void expandElement(Object element, int level)
  {
    itemProvider.expandElement(element, level);
  }

  public static ImageDescriptor getAddImageDescriptor()
  {
    return SharedIcons.getDescriptor(SharedIcons.ETOOL_ADD);
  }

  public static ImageDescriptor getDeleteImageDescriptor()
  {
    return SharedIcons.getDescriptor(SharedIcons.ETOOL_DELETE);
  }

  public static ImageDescriptor getRefreshImageDescriptor()
  {
    return SharedIcons.getDescriptor(SharedIcons.ETOOL_REFRESH);
  }

  /**
   * @since 3.3
   */
  public static ImageDescriptor getCollapseAllImageDescriptor()
  {
    return SharedIcons.getDescriptor(SharedIcons.ETOOL_COLLAPSE_ALL);
  }

  protected static enum MessageType
  {
    INFORMATION, ERROR, WARNING, CONFIRM, QUESTION
  }

  /**
   * @author Eike Stepper
   */
  private final class RefreshAction extends SafeAction
  {
    private RefreshAction()
    {
      super(Messages.getString("ContainerView_1"), Messages.getString("ContainerView_2"), getRefreshImageDescriptor()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    protected void safeRun() throws Exception
    {
      refreshPressed();
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class CollapseAllAction extends SafeAction
  {
    private CollapseAllAction()
    {
      super(Messages.getString("ContainerView_3"), Messages.getString("ContainerView_4"), //$NON-NLS-1$ //$NON-NLS-2$
          getCollapseAllImageDescriptor());
    }

    @Override
    protected void safeRun() throws Exception
    {
      collapseAllPressed();
    }
  }

  /**
   * @author Eike Stepper
   * @since 3.0
   */
  public static class Default<CONTAINER extends IContainer<?>> extends ContainerView
  {
    private CONTAINER container;

    public Default()
    {
    }

    protected IListener getContainerListener()
    {
      return null;
    }

    @Override
    protected CONTAINER getContainer()
    {
      return container;
    }

    public void setContainer(CONTAINER container)
    {
      if (this.container != container)
      {
        IListener containerListener = getContainerListener();
        if (containerListener != null && this.container != null)
        {
          this.container.removeListener(containerListener);
        }

        this.container = container;
        if (containerListener != null && this.container != null)
        {
          this.container.addListener(containerListener);
        }

        resetInput();
      }
    }
  }
}
