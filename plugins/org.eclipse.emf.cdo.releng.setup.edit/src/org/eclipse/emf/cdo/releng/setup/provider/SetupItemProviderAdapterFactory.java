/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.provider;

import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.util.SetupAdapterFactory;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.ChildCreationExtenderManager;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.IChildCreationExtender;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This is the factory that is used to provide the interfaces needed to support Viewers.
 * The adapters generated by this factory convert EMF adapter notifications into calls to {@link #fireNotifyChanged fireNotifyChanged}.
 * The adapters also support Eclipse property sheets.
 * Note that most of the adapters are shared among multiple instances.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class SetupItemProviderAdapterFactory extends SetupAdapterFactory implements ComposeableAdapterFactory,
    IChangeNotifier, IDisposable, IChildCreationExtender
{
  /**
   * This keeps track of the root adapter factory that delegates to this adapter factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ComposedAdapterFactory parentAdapterFactory;

  /**
   * This is used to implement {@link org.eclipse.emf.edit.provider.IChangeNotifier}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected IChangeNotifier changeNotifier = new ChangeNotifier();

  /**
   * This helps manage the child creation extenders.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ChildCreationExtenderManager childCreationExtenderManager = new ChildCreationExtenderManager(
      SetupEditPlugin.INSTANCE, SetupPackage.eNS_URI);

  /**
   * This keeps track of all the supported types checked by {@link #isFactoryForType isFactoryForType}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected Collection<Object> supportedTypes = new ArrayList<Object>();

  /**
   * This constructs an instance.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SetupItemProviderAdapterFactory()
  {
    supportedTypes.add(IEditingDomainItemProvider.class);
    supportedTypes.add(IStructuredItemContentProvider.class);
    supportedTypes.add(ITreeItemContentProvider.class);
    supportedTypes.add(IItemLabelProvider.class);
    supportedTypes.add(IItemPropertySource.class);
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.Configuration} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ConfigurationItemProvider configurationItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.Configuration}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createConfigurationAdapter()
  {
    if (configurationItemProvider == null)
    {
      configurationItemProvider = new ConfigurationItemProvider(this);
    }

    return configurationItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.Project} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ProjectItemProvider projectItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.Project}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createProjectAdapter()
  {
    if (projectItemProvider == null)
    {
      projectItemProvider = new ProjectItemProvider(this);
    }

    return projectItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.Branch} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected BranchItemProvider branchItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.Branch}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createBranchAdapter()
  {
    if (branchItemProvider == null)
    {
      branchItemProvider = new BranchItemProvider(this);
    }

    return branchItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.ApiBaselineTask} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ApiBaselineTaskItemProvider apiBaselineTaskItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.ApiBaselineTask}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createApiBaselineTaskAdapter()
  {
    if (apiBaselineTaskItemProvider == null)
    {
      apiBaselineTaskItemProvider = new ApiBaselineTaskItemProvider(this);
    }

    return apiBaselineTaskItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.GitCloneTask} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected GitCloneTaskItemProvider gitCloneTaskItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.GitCloneTask}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createGitCloneTaskAdapter()
  {
    if (gitCloneTaskItemProvider == null)
    {
      gitCloneTaskItemProvider = new GitCloneTaskItemProvider(this);
    }

    return gitCloneTaskItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.EclipseVersion} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EclipseVersionItemProvider eclipseVersionItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.EclipseVersion}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createEclipseVersionAdapter()
  {
    if (eclipseVersionItemProvider == null)
    {
      eclipseVersionItemProvider = new EclipseVersionItemProvider(this);
    }

    return eclipseVersionItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.P2Task} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected P2TaskItemProvider p2TaskItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.P2Task}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createP2TaskAdapter()
  {
    if (p2TaskItemProvider == null)
    {
      p2TaskItemProvider = new P2TaskItemProvider(this);
    }

    return p2TaskItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.InstallableUnit} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected InstallableUnitItemProvider installableUnitItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.InstallableUnit}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createInstallableUnitAdapter()
  {
    if (installableUnitItemProvider == null)
    {
      installableUnitItemProvider = new InstallableUnitItemProvider(this);
    }

    return installableUnitItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.P2Repository} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected P2RepositoryItemProvider p2RepositoryItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.P2Repository}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createP2RepositoryAdapter()
  {
    if (p2RepositoryItemProvider == null)
    {
      p2RepositoryItemProvider = new P2RepositoryItemProvider(this);
    }

    return p2RepositoryItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.Setup} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected SetupItemProvider setupItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.Setup}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createSetupAdapter()
  {
    if (setupItemProvider == null)
    {
      setupItemProvider = new SetupItemProvider(this);
    }

    return setupItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.WorkingSetTask} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected WorkingSetTaskItemProvider workingSetTaskItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.WorkingSetTask}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createWorkingSetTaskAdapter()
  {
    if (workingSetTaskItemProvider == null)
    {
      workingSetTaskItemProvider = new WorkingSetTaskItemProvider(this);
    }

    return workingSetTaskItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.ResourceCopyTask} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ResourceCopyTaskItemProvider resourceCopyTaskItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.ResourceCopyTask}.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createResourceCopyTaskAdapter()
  {
    if (resourceCopyTaskItemProvider == null)
    {
      resourceCopyTaskItemProvider = new ResourceCopyTaskItemProvider(this);
    }

    return resourceCopyTaskItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.TextModifyTask} instances.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  protected TextModifyTaskItemProvider textModifyTaskItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.TextModifyTask}.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createTextModifyTaskAdapter()
  {
    if (textModifyTaskItemProvider == null)
    {
      textModifyTaskItemProvider = new TextModifyTaskItemProvider(this);
    }

    return textModifyTaskItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.TextModification} instances.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  protected TextModificationItemProvider textModificationItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.TextModification}.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createTextModificationAdapter()
  {
    if (textModificationItemProvider == null)
    {
      textModificationItemProvider = new TextModificationItemProvider(this);
    }

    return textModificationItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.KeyBindingTask} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected KeyBindingTaskItemProvider keyBindingTaskItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.KeyBindingTask}.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createKeyBindingTaskAdapter()
  {
    if (keyBindingTaskItemProvider == null)
    {
      keyBindingTaskItemProvider = new KeyBindingTaskItemProvider(this);
    }

    return keyBindingTaskItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.CommandParameter} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected CommandParameterItemProvider commandParameterItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.CommandParameter}.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createCommandParameterAdapter()
  {
    if (commandParameterItemProvider == null)
    {
      commandParameterItemProvider = new CommandParameterItemProvider(this);
    }

    return commandParameterItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.AutomaticSourceLocator} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected AutomaticSourceLocatorItemProvider automaticSourceLocatorItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.AutomaticSourceLocator}.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createAutomaticSourceLocatorAdapter()
  {
    if (automaticSourceLocatorItemProvider == null)
    {
      automaticSourceLocatorItemProvider = new AutomaticSourceLocatorItemProvider(this);
    }

    return automaticSourceLocatorItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.ManualSourceLocator} instances.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  protected ManualSourceLocatorItemProvider manualSourceLocatorItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.ManualSourceLocator}.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createManualSourceLocatorAdapter()
  {
    if (manualSourceLocatorItemProvider == null)
    {
      manualSourceLocatorItemProvider = new ManualSourceLocatorItemProvider(this);
    }

    return manualSourceLocatorItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.ContextVariableTask} instances.
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @generated
   */
  protected ContextVariableTaskItemProvider contextVariableTaskItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.ContextVariableTask}.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createContextVariableTaskAdapter()
  {
    if (contextVariableTaskItemProvider == null)
    {
      contextVariableTaskItemProvider = new ContextVariableTaskItemProvider(this);
    }

    return contextVariableTaskItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.ResourceCreationTask} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ResourceCreationTaskItemProvider resourceCreationTaskItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.ResourceCreationTask}.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createResourceCreationTaskAdapter()
  {
    if (resourceCreationTaskItemProvider == null)
    {
      resourceCreationTaskItemProvider = new ResourceCreationTaskItemProvider(this);
    }

    return resourceCreationTaskItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.MaterializationTask} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected MaterializationTaskItemProvider materializationTaskItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.MaterializationTask}.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createMaterializationTaskAdapter()
  {
    if (materializationTaskItemProvider == null)
    {
      materializationTaskItemProvider = new MaterializationTaskItemProvider(this);
    }

    return materializationTaskItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.Component} instances.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  protected ComponentItemProvider componentItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.Component}.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createComponentAdapter()
  {
    if (componentItemProvider == null)
    {
      componentItemProvider = new ComponentItemProvider(this);
    }

    return componentItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.EclipseIniTask} instances.
   * <!-- begin-user-doc -->
               * <!-- end-user-doc -->
   * @generated
   */
  protected EclipseIniTaskItemProvider eclipseIniTaskItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.EclipseIniTask}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createEclipseIniTaskAdapter()
  {
    if (eclipseIniTaskItemProvider == null)
    {
      eclipseIniTaskItemProvider = new EclipseIniTaskItemProvider(this);
    }

    return eclipseIniTaskItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.CompoundSetupTask} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected CompoundSetupTaskItemProvider compoundSetupTaskItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.CompoundSetupTask}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createCompoundSetupTaskAdapter()
  {
    if (compoundSetupTaskItemProvider == null)
    {
      compoundSetupTaskItemProvider = new CompoundSetupTaskItemProvider(this);
    }

    return compoundSetupTaskItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.BuckminsterImportTask} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected BuckminsterImportTaskItemProvider buckminsterImportTaskItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.BuckminsterImportTask}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createBuckminsterImportTaskAdapter()
  {
    if (buckminsterImportTaskItemProvider == null)
    {
      buckminsterImportTaskItemProvider = new BuckminsterImportTaskItemProvider(this);
    }

    return buckminsterImportTaskItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.StringVariableTask} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected StringVariableTaskItemProvider stringVariableTaskItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.StringVariableTask}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createStringVariableTaskAdapter()
  {
    if (stringVariableTaskItemProvider == null)
    {
      stringVariableTaskItemProvider = new StringVariableTaskItemProvider(this);
    }

    return stringVariableTaskItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.Preferences} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected PreferencesItemProvider preferencesItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.Preferences}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createPreferencesAdapter()
  {
    if (preferencesItemProvider == null)
    {
      preferencesItemProvider = new PreferencesItemProvider(this);
    }

    return preferencesItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.LinkLocationTask} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected LinkLocationTaskItemProvider linkLocationTaskItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.LinkLocationTask}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createLinkLocationTaskAdapter()
  {
    if (linkLocationTaskItemProvider == null)
    {
      linkLocationTaskItemProvider = new LinkLocationTaskItemProvider(this);
    }

    return linkLocationTaskItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.releng.setup.EclipsePreferenceTask} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EclipsePreferenceTaskItemProvider eclipsePreferenceTaskItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.releng.setup.EclipsePreferenceTask}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createEclipsePreferenceTaskAdapter()
  {
    if (eclipsePreferenceTaskItemProvider == null)
    {
      eclipsePreferenceTaskItemProvider = new EclipsePreferenceTaskItemProvider(this);
    }

    return eclipsePreferenceTaskItemProvider;
  }

  /**
   * This returns the root adapter factory that contains this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ComposeableAdapterFactory getRootAdapterFactory()
  {
    return parentAdapterFactory == null ? this : parentAdapterFactory.getRootAdapterFactory();
  }

  /**
   * This sets the composed adapter factory that contains this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setParentAdapterFactory(ComposedAdapterFactory parentAdapterFactory)
  {
    this.parentAdapterFactory = parentAdapterFactory;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isFactoryForType(Object type)
  {
    return supportedTypes.contains(type) || super.isFactoryForType(type);
  }

  /**
   * This implementation substitutes the factory itself as the key for the adapter.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter adapt(Notifier notifier, Object type)
  {
    return super.adapt(notifier, this);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object adapt(Object object, Object type)
  {
    if (isFactoryForType(type))
    {
      Object adapter = super.adapt(object, type);
      if (!(type instanceof Class<?>) || ((Class<?>)type).isInstance(adapter))
      {
        return adapter;
      }
    }

    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public List<IChildCreationExtender> getChildCreationExtenders()
  {
    return childCreationExtenderManager.getChildCreationExtenders();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Collection<?> getNewChildDescriptors(Object object, EditingDomain editingDomain)
  {
    return childCreationExtenderManager.getNewChildDescriptors(object, editingDomain);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ResourceLocator getResourceLocator()
  {
    return childCreationExtenderManager;
  }

  /**
   * This adds a listener.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void addListener(INotifyChangedListener notifyChangedListener)
  {
    changeNotifier.addListener(notifyChangedListener);
  }

  /**
   * This removes a listener.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void removeListener(INotifyChangedListener notifyChangedListener)
  {
    changeNotifier.removeListener(notifyChangedListener);
  }

  /**
   * This delegates to {@link #changeNotifier} and to {@link #parentAdapterFactory}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void fireNotifyChanged(Notification notification)
  {
    changeNotifier.fireNotifyChanged(notification);

    if (parentAdapterFactory != null)
    {
      parentAdapterFactory.fireNotifyChanged(notification);
    }
  }

  /**
   * This disposes all of the item providers created by this factory. 
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void dispose()
  {
    if (eclipseVersionItemProvider != null)
    {
      eclipseVersionItemProvider.dispose();
    }
    if (configurationItemProvider != null)
    {
      configurationItemProvider.dispose();
    }
    if (projectItemProvider != null)
    {
      projectItemProvider.dispose();
    }
    if (branchItemProvider != null)
    {
      branchItemProvider.dispose();
    }
    if (preferencesItemProvider != null)
    {
      preferencesItemProvider.dispose();
    }
    if (setupItemProvider != null)
    {
      setupItemProvider.dispose();
    }
    if (compoundSetupTaskItemProvider != null)
    {
      compoundSetupTaskItemProvider.dispose();
    }
    if (eclipseIniTaskItemProvider != null)
    {
      eclipseIniTaskItemProvider.dispose();
    }
    if (linkLocationTaskItemProvider != null)
    {
      linkLocationTaskItemProvider.dispose();
    }
    if (p2TaskItemProvider != null)
    {
      p2TaskItemProvider.dispose();
    }
    if (installableUnitItemProvider != null)
    {
      installableUnitItemProvider.dispose();
    }
    if (p2RepositoryItemProvider != null)
    {
      p2RepositoryItemProvider.dispose();
    }
    if (buckminsterImportTaskItemProvider != null)
    {
      buckminsterImportTaskItemProvider.dispose();
    }
    if (materializationTaskItemProvider != null)
    {
      materializationTaskItemProvider.dispose();
    }
    if (componentItemProvider != null)
    {
      componentItemProvider.dispose();
    }
    if (contextVariableTaskItemProvider != null)
    {
      contextVariableTaskItemProvider.dispose();
    }
    if (apiBaselineTaskItemProvider != null)
    {
      apiBaselineTaskItemProvider.dispose();
    }
    if (gitCloneTaskItemProvider != null)
    {
      gitCloneTaskItemProvider.dispose();
    }
    if (eclipsePreferenceTaskItemProvider != null)
    {
      eclipsePreferenceTaskItemProvider.dispose();
    }
    if (stringVariableTaskItemProvider != null)
    {
      stringVariableTaskItemProvider.dispose();
    }
    if (workingSetTaskItemProvider != null)
    {
      workingSetTaskItemProvider.dispose();
    }
    if (resourceCopyTaskItemProvider != null)
    {
      resourceCopyTaskItemProvider.dispose();
    }
    if (resourceCreationTaskItemProvider != null)
    {
      resourceCreationTaskItemProvider.dispose();
    }
    if (textModifyTaskItemProvider != null)
    {
      textModifyTaskItemProvider.dispose();
    }
    if (textModificationItemProvider != null)
    {
      textModificationItemProvider.dispose();
    }
    if (keyBindingTaskItemProvider != null)
    {
      keyBindingTaskItemProvider.dispose();
    }
    if (commandParameterItemProvider != null)
    {
      commandParameterItemProvider.dispose();
    }
    if (manualSourceLocatorItemProvider != null)
    {
      manualSourceLocatorItemProvider.dispose();
    }
    if (automaticSourceLocatorItemProvider != null)
    {
      automaticSourceLocatorItemProvider.dispose();
    }
  }

}
