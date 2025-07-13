package com.example.management.infrastructure.persistence.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class    FileUtils {

    public static void initFile(Path path, String header) throws IOException {

        if(!Files.exists(path.getParent())){
            Files.createDirectory(path.getParent());
        }

        if(!Files.exists(path)){
            Files.createFile(path);

            try(BufferedWriter bw = Files.newBufferedWriter(path)){
                bw.write(header + "\n");
            }

        }
    }

    public static long getNextId(Path path) throws IOException {
        long maxId = 0;

        try(BufferedReader br = Files.newBufferedReader(path)){
            br.readLine();
            String line;

            while ((line = br.readLine()) != null){
                String[] parts = line.split(";");

                if(parts.length > 0){

                    try{
                        long id = Long.parseLong(parts[0]);
                        if(id > maxId) maxId = id;

                    } catch (NumberFormatException e) {
                        System.out.println("Error parsing ID");
                    }
                }
            }
        }
        return maxId + 1;
    }

    public static List<String[]> readAllRows(Path path) throws IOException{
        List<String[]> rows = new ArrayList<>();

        try(BufferedReader br = Files.newBufferedReader(path)){
            br.readLine();
            String line;

            while ((line = br.readLine()) != null){
                rows.add(line.split(";"));
            }
        }
        return rows;
    }



}
