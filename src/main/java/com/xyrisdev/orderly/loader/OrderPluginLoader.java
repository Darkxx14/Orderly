package com.xyrisdev.orderly.loader;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class OrderPluginLoader implements PluginLoader {

	@Override
	public void classloader(@NotNull PluginClasspathBuilder builder) {
		final MavenLibraryResolver resolver = new MavenLibraryResolver();

		resolver.addRepository(new RemoteRepository.Builder("papermc-repo", "default", "https://repo.papermc.io/repository/maven-public/").build());
		resolver.addRepository(new RemoteRepository.Builder("sonatype", "default", "https://oss.sonatype.org/content/groups/public/").build());
		resolver.addRepository(new RemoteRepository.Builder("xyris-repo", "default", "https://repo.xyrisdev.com/repository/maven-public").build());

		resolver.addDependency(new Dependency(new DefaultArtifact("com.zaxxer:HikariCP:7.0.2"), null));

		builder.addLibrary(resolver);
	}
}
