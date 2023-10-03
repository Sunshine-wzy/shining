package io.github.sunshinewzy.shining.api.guide.team;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CompletedGuideTeam implements IGuideTeam {

	private static final CompletedGuideTeam instance = new CompletedGuideTeam();
	
	private CompletedGuideTeam() {}
	
	public static CompletedGuideTeam getInstance() {
		return instance;
	}
	
	
	@NotNull
	@Override
	public CompletableFuture<Boolean> joinFuture(@NotNull Player player) {
		return new CompletableFuture<>();
	}

	@NotNull
	@Override
	public CompletableFuture<Boolean> joinFuture(@NotNull UUID uuid) {
		return new CompletableFuture<>();
	}

	@NotNull
	@Override
	public CompletableFuture<Boolean> leaveFuture(@NotNull Player player) {
		return new CompletableFuture<>();
	}

	@NotNull
	@Override
	public CompletableFuture<Boolean> leaveFuture(@NotNull UUID uuid) {
		return new CompletableFuture<>();
	}

	@NotNull
	@Override
	public CompletableFuture<Boolean> applyFuture(@NotNull Player player) {
		return new CompletableFuture<>();
	}

	@NotNull
	@Override
	public CompletableFuture<Boolean> applyFuture(@NotNull UUID uuid) {
		return new CompletableFuture<>();
	}

	@NotNull
	@Override
	public CompletableFuture<Boolean> changeCaptainFuture(@NotNull Player player) {
		return new CompletableFuture<>();
	}

	@NotNull
	@Override
	public CompletableFuture<Boolean> changeCaptainFuture(@NotNull UUID uuid) {
		return new CompletableFuture<>();
	}

	@NotNull
	@Override
	public CompletableFuture<Boolean> changeNameFuture(@NotNull String name) {
		return new CompletableFuture<>();
	}

	@NotNull
	@Override
	public CompletableFuture<IGuideTeamData> getTeamDataFuture() {
		return new CompletableFuture<>();
	}

	@NotNull
	@Override
	public CompletableFuture<Boolean> updateTeamDataFuture() {
		return new CompletableFuture<>();
	}

	@NotNull
	@Override
	public CompletableFuture<Boolean> approveApplicationFuture(@NotNull UUID uuid) {
		return new CompletableFuture<>();
	}

	@NotNull
	@Override
	public CompletableFuture<Boolean> refuseApplicationFuture(@NotNull UUID uuid) {
		return new CompletableFuture<>();
	}

	@Override
	public void notifyCaptainApplication() {}

	@NotNull
	@Override
	public List<Player> getOnlinePlayers() {
		return Collections.emptyList();
	}

	@Override
	public void welcome(@NotNull UUID uuid) {}

	@Override
	public void openInfoMenu(@NotNull Player player) {}

	@Override
	public void openManageMenu(@NotNull Player player) {}

	@Override
	public void openManageApplicationMenu(@NotNull Player player) {}
	
}
