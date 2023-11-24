package org.lessons.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Main {
	private static final String url = "jdbc:mysql://localhost:3306/db-nations";
	private static final String user = "root";
	private static final String pws = "";
	
	public static void main(String[] args) {
		Scanner in= new Scanner(System.in);
		System.out.print("Filtra i paesi per nome?: ");
		String strName = in.nextLine();
		
	
		try (Connection con 
		      = DriverManager.getConnection(url, user, pws)) {  
		  
			  final String sql = "SELECT countries.name , countries.country_id, regions.name, continents.name "
						 +" FROM countries "
						 +" JOIN regions "
						 +" ON countries.region_id = regions.region_id "
						 +" JOIN continents"
						 +" ON regions.continent_id = continents.continent_id "
						 + "WHERE countries.name LIKE ?"
						 + " ORDER BY countries.name ASC"
						 +" ; ";  
		  
		  try(PreparedStatement ps = con.prepareStatement(sql)){
			  
			  ps.setString(1, "%" + strName + "%");
			  
		    try(ResultSet rs = ps.executeQuery()){
		    	
		    	while(rs.next()) {
		    		
		    		String name = rs.getString(1);
		    		int id = rs.getInt(2);
		    		String regionsName = rs.getString(3);
		    		String continentName = rs.getString(4);
		    		
		    		System.out.println("NOME NAZIONE: " + name + " -- " + "ID NAZIONE: "+ id + " -- " 
    						+ "NOME REGIONE: " + regionsName + " -- " + "NOME CONTINENTE: " 
    						+ continentName );
		    	}
		    }
		  }
		  
		  System.out.print("Inserisci l'id di una country per ulteriori informazioni: ");
          int selectedCountryId = in.nextInt();
          in.close();

          // Query per ottenere tutte le lingue parlate in quella country
          final String languagesSql = "SELECT languages.language "
                  + "FROM countries "
                  + "JOIN country_languages ON countries.country_id = country_languages.country_id "
                  + "JOIN languages ON country_languages.language_id = languages.language_id "
                  + "WHERE countries.country_id = ?";
          
          try (PreparedStatement languagesPs = con.prepareStatement(languagesSql)) {
              languagesPs.setInt(1, selectedCountryId);

              try (ResultSet languagesRs = languagesPs.executeQuery()) {
                  System.out.println("Lingue parlate nella country con ID " + selectedCountryId + ":");
                  while (languagesRs.next()) {
                      String language = languagesRs.getString(1);
                      System.out.println(language);
                  }
              }
          }

          // Query per ottenere le statistiche più recenti per quella country
          final String statsSql = "SELECT country_stats.year, country_stats.population, country_stats.gdp "
                  + "FROM countries "
                  + "JOIN country_stats ON countries.country_id = country_stats.country_id "
                  + "WHERE countries.country_id = ? "
                  + "ORDER BY country_stats.year DESC "
                  + "LIMIT 1";
          
          try (PreparedStatement statsPs = con.prepareStatement(statsSql)) {
              statsPs.setInt(1, selectedCountryId);

              try (ResultSet statsRs = statsPs.executeQuery()) {
                  System.out.println("Ultime statistiche per la country con ID " + selectedCountryId + ":");
                  while (statsRs.next()) {
                      int year = statsRs.getInt("year");
                      int population = statsRs.getInt("population");
                      double gdp = statsRs.getDouble("gdp");

                      System.out.println("Anno: " + year);
                      System.out.println("Popolazione: " + population);
                      System.out.println("GDP: " + gdp);
                  }
              }
          }
		  
		} catch (Exception e) {
			
			System.out.println("Error in db: " + e.getMessage());
		}
	}		
			
}

