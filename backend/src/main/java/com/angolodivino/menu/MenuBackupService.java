package com.angolodivino.menu;

import java.time.LocalDate;
import java.time.ZoneId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MenuBackupService {
    static final ZoneId ROME = ZoneId.of("Europe/Rome");
    private static final Logger log = LoggerFactory.getLogger(MenuBackupService.class);
    private final MenuOverridesStore store;

    public MenuBackupService(MenuOverridesStore store) {
        this.store = store;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void recoverMissingBackupsAtStartup() {
        runBackup();
    }

    @Scheduled(cron = "${app.menu.backup-cron:0 15 2 * * *}", zone = "Europe/Rome")
    public void scheduledBackup() {
        runBackup();
    }

    void runBackup() {
        try {
            store.maintainBackups(LocalDate.now(ROME));
        } catch (RuntimeException e) {
            log.error("Menu backup maintenance failed; the runtime menu was left unchanged", e);
        }
    }
}
