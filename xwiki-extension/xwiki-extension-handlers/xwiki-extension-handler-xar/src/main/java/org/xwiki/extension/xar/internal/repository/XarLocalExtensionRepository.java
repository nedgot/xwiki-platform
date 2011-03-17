/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.extension.xar.internal.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.Requirement;
import org.xwiki.component.logging.AbstractLogEnabled;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.extension.Extension;
import org.xwiki.extension.ExtensionId;
import org.xwiki.extension.InstallException;
import org.xwiki.extension.LocalExtension;
import org.xwiki.extension.ResolveException;
import org.xwiki.extension.UninstallException;
import org.xwiki.extension.event.ExtensionInstalled;
import org.xwiki.extension.event.ExtensionUninstalled;
import org.xwiki.extension.event.ExtensionUpgraded;
import org.xwiki.extension.repository.ExtensionRepositoryId;
import org.xwiki.extension.repository.LocalExtensionRepository;
import org.xwiki.extension.xar.internal.handler.packager.Packager;
import org.xwiki.observation.EventListener;
import org.xwiki.observation.ObservationManager;
import org.xwiki.observation.event.Event;

@Component("xar")
public class XarLocalExtensionRepository extends AbstractLogEnabled implements LocalExtensionRepository, Initializable
{
    private static final List<Event> EVENTS = Arrays.<Event> asList(new ExtensionInstalled(),
        new ExtensionUninstalled(), new ExtensionUpgraded());

    @Requirement
    private LocalExtensionRepository localRepository;

    @Requirement
    private Packager packager;

    @Requirement
    private ObservationManager observation;

    private ExtensionRepositoryId repositoryId;

    private Map<ExtensionId, XarLocalExtension> extensions = new ConcurrentHashMap<ExtensionId, XarLocalExtension>();

    public void initialize() throws InitializationException
    {
        this.repositoryId = new ExtensionRepositoryId("xar", "xar", this.localRepository.getId().getURI());

        loadExtensions();

        this.observation.addListener(new EventListener()
        {
            public void onEvent(Event event, Object arg1, Object arg2)
            {
                LocalExtension extension = (LocalExtension) arg1;
                if (extension.getType().equals("xar")) {
                    updateXarExtension(extension);
                }

                if (arg2 != null) {
                    updateXarExtension((LocalExtension) arg2);
                }
            }

            public String getName()
            {
                return XarLocalExtensionRepository.class.getName();
            }

            public List<Event> getEvents()
            {
                return EVENTS;
            }
        });
    }

    private void updateXarExtension(LocalExtension extension)
    {
        if (this.extensions.containsKey(extension.getId())) {
            if (!extension.isInstalled()) {
                removeXarExtension(extension.getId());
            }
        } else {
            if (extension.isInstalled()) {
                try {
                    addXarExtension(extension);
                } catch (IOException e) {
                    getLogger().error("Failed to parse extension [" + extension + "]", e);
                }
            }
        }
    }

    private void addXarExtension(LocalExtension extension) throws IOException
    {
        XarLocalExtension xarExtension = new XarLocalExtension(extension, this, this.packager);

        this.extensions.put(extension.getId(), xarExtension);
    }

    private void removeXarExtension(ExtensionId extensionId)
    {
        this.extensions.remove(extensionId);
    }

    private void loadExtensions()
    {
        for (LocalExtension localExtension : this.localRepository.getLocalExtensions()) {
            if (localExtension.getType().equalsIgnoreCase("xar")) {
                try {
                    addXarExtension(localExtension);
                } catch (IOException e) {
                    getLogger().error("Failed to parse extension [" + localExtension + "]", e);
                }
            }
        }
    }

    // LocalExtensionRepository

    public ExtensionRepositoryId getId()
    {
        return this.repositoryId;
    }

    public Extension resolve(ExtensionId extensionId) throws ResolveException
    {
        Extension extension = this.extensions.get(extensionId);

        if (extension == null) {
            throw new ResolveException("Extension [" + extensionId + "] does not exists or is not a xar extension");
        }

        return extension;
    }

    public boolean exists(ExtensionId extensionId)
    {
        return this.extensions.containsKey(extensionId);
    }

    public int countExtensions()
    {
        return this.extensions.size();
    }

    public Collection< ? extends Extension> getExtensions(int nb, int offset)
    {
        return new ArrayList<LocalExtension>(this.extensions.values()).subList(offset, offset + nb);
    }

    public Collection<LocalExtension> getLocalExtensions()
    {
        return Collections.<LocalExtension> unmodifiableCollection(this.extensions.values());
    }

    public Collection<LocalExtension> getInstalledExtensions(String namespace)
    {
        List<LocalExtension> installedExtensions = new ArrayList<LocalExtension>(extensions.size());
        for (LocalExtension localExtension : this.extensions.values()) {
            if (localExtension.isInstalled(namespace)) {
                installedExtensions.add(localExtension);
            }
        }

        return installedExtensions;
    }

    public Collection<LocalExtension> getInstalledExtensions()
    {
        return getLocalExtensions();
    }

    public LocalExtension getInstalledExtension(String id, String namespace)
    {
        LocalExtension extension = this.localRepository.getInstalledExtension(id, namespace);

        if (extension.getType().equals("xar")) {
            extension = this.extensions.get(extension.getId());
        } else {
            extension = null;
        }

        return extension;
    }

    public LocalExtension installExtension(Extension extension, boolean dependency, String namespace)
        throws InstallException
    {
        throw new InstallException("Not implemented");
    }

    public void uninstallExtension(LocalExtension extension, String namespace) throws UninstallException
    {
        throw new UninstallException("Not implemented");
    }

    public Collection<LocalExtension> getBackwardDependencies(String id, String namespace) throws ResolveException
    {
        LocalExtension extension = this.localRepository.getInstalledExtension(id, namespace);

        return extension.getType().equals("xar") ? this.localRepository.getBackwardDependencies(id, namespace) : null;
    }

    public Map<String, Collection<LocalExtension>> getBackwardDependencies(ExtensionId extensionId)
        throws ResolveException
    {
        LocalExtension extension = (LocalExtension) this.localRepository.resolve(extensionId);

        return extension.getType().equals("xar") ? this.localRepository.getBackwardDependencies(extensionId) : null;
    }
}
