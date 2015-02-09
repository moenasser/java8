package com.mnasser.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Filter {

	public static void main(String[] args) {
		try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
			
			br.readLine(); // xyz file deal1,e
			int fields = Integer.parseInt( br.readLine().split("\\s+")[1] );
			int max = Integer.parseInt( br.readLine().split("\\s+")[1] );
			int min = Integer.parseInt( br.readLine().split("\\s+")[1] );
			String s = br.readLine();
			boolean nl = false;
			nl = s.equalsIgnoreCase("end_with_newline") ;
			
			br.readLine(); // soft - not sure what this is
			
			char delim = ',';
			StringBuilder sb = new StringBuilder();
			StringBuilder header = new StringBuilder();
			
			boolean haveHeader = false;
			int h = 0 ;
			
			
			String line = null;
			while( (line=br.readLine()) != null  ){
				String[] parts = line.split("\\s+");
				h++;
				if ( ! haveHeader ){
					header.append(parts[1]);
					if (h % fields != 0) 
						header.append(delim);
				}
				sb.append(parts[3].replace("\"", ""));
				if( h % fields != 0 )
					sb.append(delim);
				
				// on exactly the 11th line print header
				if ( h == fields ) {
					if( nl ) header.append('\n');
					System.out.println( header.toString() );
					haveHeader = true;
					header = null;
				}
				
				if ( h % fields == 0 ){
					if ( nl ) sb.append('\n');
					System.out.println( sb.toString() );
				}
			}
			
		}catch(Exception e){e.printStackTrace();}
	}
}
