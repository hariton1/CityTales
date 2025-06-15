// src/app/services/fun-fact.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Observable, of} from 'rxjs';
import { BACKEND_ADDRESS } from '../globals'; // Pfad ggf. anpassen

export interface FunFactCardDTO {

  sentence: string;
  score: number;
  reason: string;
}



@Injectable({
  providedIn: 'root'
})
export class FunFactService {
  private apiUrl = `${BACKEND_ADDRESS}api/funfact`;
  private useDummy = false;

  constructor(private http: HttpClient) {}

  getBuildingFunFact(buildingId: number): Observable<FunFactCardDTO> {
    if (this.useDummy) {
      return of({

        sentence: `Dies ist ein Dummy-Fun-Fact für Gebäude #${buildingId}.`,
        score: Math.floor(Math.random() * 100),
        reason: `Darum`
      });
    } else {
      return this.http.get<FunFactCardDTO>(`${this.apiUrl}/building/${buildingId}`);
    }

  }
  getPersonFunFact(personId: number): Observable<FunFactCardDTO> {
    if (this.useDummy) {
      return of({
        sentence: `Dies ist ein Dummy-Fun-Fact für person #${personId}.`,
        score: Math.floor(Math.random() * 100),
        reason: `Darum`
      });
    } else {
      return this.http.get<FunFactCardDTO>(`${this.apiUrl}/person/${personId}`);
    }

  }
  getEventFunFact(eventId: number): Observable<FunFactCardDTO> {
    if (this.useDummy) {
      return of({
        sentence: `Dies ist ein Dummy-Fun-Fact für event #${eventId}.`,
        score: Math.floor(Math.random() * 100),
        reason: `Darum`
      });
    } else {
      return this.http.get<FunFactCardDTO>(`${this.apiUrl}/event/${eventId}`);
    }

  }
}
