package com.keillen.ShelfLog.config;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.keillen.ShelfLog.repositories.MediaRepository;
import com.keillen.ShelfLog.services.MediaImportService;

@Component
public class ImportRunner implements CommandLineRunner {

    private final MediaImportService importService;
    private final MediaRepository mediaRepository;
    public ImportRunner(MediaImportService importService, MediaRepository mediaRepository) { 
        
        this.importService = importService; 
        this.mediaRepository = mediaRepository;

    }
    @Override
    public void run(String... args) { 
        if (mediaRepository.count() == 0){
            importService.importAll(); 
        }
    }
}