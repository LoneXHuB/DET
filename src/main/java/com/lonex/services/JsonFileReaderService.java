package com.lonex.services;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lonex.enums.MachineType;
import com.lonix.det.models.ClientAction;
import com.lonix.det.models.Machine;
import com.lonix.det.models.MachineCategory;

@Service
public class JsonFileReaderService {

	private ObjectMapper objectMapper = new ObjectMapper();
	
	public List<MachineCategory> getMachineTypesFromJson (MachineType machineType) {
		try {
		
			List<MachineCategory> machines ;
			
			switch ( machineType) {
			case  Centre :
				 machines = objectMapper.readValue(new File("src/main/resources/data/centre.json"), new TypeReference<List<MachineCategory>>(){});
				break;
			case Tour :
				 machines = objectMapper.readValue(new File("src/main/resources/data/tour.json"), new TypeReference<List<MachineCategory>>(){});
				break;
			default :
				machines=null;
			}
			

			return machines;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println( "failed to read Json file" );
			return null;
		}
	}
	
	
	
	public List<MachineCategory> getSearchMachineCategoryList(MachineType category , String searchQuery){
			
			List<MachineCategory> allMachines = this.getMachineTypesFromJson(category);
			List<MachineCategory> searchResult = new ArrayList<MachineCategory> ();
			
			char[] searchQueryArray = searchQuery.toUpperCase().toCharArray();
			
			if(searchQueryArray.length==0)
				return allMachines;
			
			for(MachineCategory machine : allMachines) {
				boolean allMatch = true;
				char[] mapNameArray = machine.getMapName().toUpperCase().toCharArray();
				int index = 0;
	
				for(char queryChar : searchQueryArray)
				{
				
					if(queryChar != mapNameArray[index])
					{
						allMatch=false;
						break;
					}
				
				  index++;
				}
				
				if(allMatch)
				{
					searchResult.add(machine);
				}
				
				
			}
			
			return searchResult;
		}

	public List<MachineCategory> getSearchMachineCategoryList(String searchQuery){
		
		List<MachineCategory> allMachines = this.getFullMachineCategoryList();
		List<MachineCategory> searchResult = new ArrayList<MachineCategory> ();
		
		char[] searchQueryArray = searchQuery.toUpperCase().toCharArray();
		
		if(searchQueryArray.length==0)
			return allMachines;
		
		for(MachineCategory machine : allMachines) {
			boolean allMatch = true;
			char[] mapNameArray = machine.getMapName().toUpperCase().toCharArray();
			int index = 0;

			for(char queryChar : searchQueryArray)
			{
			
				if(queryChar != mapNameArray[index])
				{
					allMatch=false;
					break;
				}
			
			  index++;
			}
			
			if(allMatch)
			{
				searchResult.add(machine);
			}
			
			
		}
		
		return searchResult;
	}
	
	
	public List<MachineCategory> getFullMachineCategoryList() {
		List<MachineCategory> centres;
		List<MachineCategory> machines;
		
		try {
				centres = objectMapper.readValue(new File("src/main/resources/data/centre.json"), new TypeReference<List<MachineCategory>>(){});
				machines = objectMapper.readValue(new File("src/main/resources/data/tour.json"), new TypeReference<List<MachineCategory>>(){});
			}catch(Exception e) {e.printStackTrace(); return null;}
		
		for(MachineCategory machine : centres)
		{
			machines.add(machine);
		}
		
		return machines;
	}
	

	public List<Machine> getMachinesFromJson() {
		try {
			return objectMapper.readValue(new File("src/main/resources/data/machines.json"), new TypeReference<List<Machine>>(){});
		
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println( "failed to read Json file" );
			return null;
		}
	}
	
	public List<ClientAction> getClientActions(String clientIp) {
		try {
			return objectMapper.readValue(new File("src/main/resources/data/"+clientIp+".json"), new TypeReference<List<ClientAction>>(){});
		
		} catch (Exception e) {
			System.out.println( "src/main/resources/data/"+clientIp+".json"+" not found" );
			return new ArrayList<ClientAction> ();
		}
	}
	
	//Left this here just in case we need it...
	public boolean WriteClientAction(HttpServletRequest request , String action) {
		String clientIp = JsonFileReaderService.getClientIp(request).replace(':', '.');
		Date currentDate = new Date();
		ClientAction clientAction = new ClientAction(clientIp , action , currentDate.toString());
		
		
		
		File clientFile= new File("src/main/resources/data/"+clientIp+".json");
		List<ClientAction> clientActions = this.getClientActions(clientIp);
		
		
		
		try {
		
			clientFile.createNewFile();
				
			
			
			clientActions.add(clientAction);
			objectMapper.writeValue(clientFile ,clientActions);
				
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("Failed to Write user actions for client : " + clientIp);
		}
		
		
		return true;
	}
	
	private static String getClientIp(HttpServletRequest request) {
		
        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        return remoteAddr;
    }
}
