// src/app/services/fun-fact.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Observable, of} from 'rxjs';

export interface FunFactCardDTO {
  id: number;
  fact: string;
  score: number;
}

@Injectable({
  providedIn: 'root'
})
export class FunFactService {
  private apiUrl = '/api/funfact';
  private useDummy = true;

  constructor(private http: HttpClient) {}

  getBuildingFunFact(buildingId: number): Observable<FunFactCardDTO> {
    if (this.useDummy) {
      // Hier kommt immer derselbe Dummy zurück (du kannst ihn beliebig anpassen)
      return of({
        id: buildingId,
        fact: `Dies ist ein Dummy-Fun-Fact für Gebäude #${buildingId}.`,
        score: Math.floor(Math.random() * 100)
      });
    } else {
      // Hier kommt später dein echter HTTP-Call hin!
      // return this.http.get<FunFactCardDTO>(`${this.apiUrl}/building/${buildingId}`);
      return this.http.get<FunFactCardDTO>(`${this.apiUrl}/building/${buildingId}`);
    }

  }
  getPersonFunFact(personId: number): Observable<FunFactCardDTO> {
    return this.http.get<FunFactCardDTO>(`${this.apiUrl}/person/${personId}`);
  }
  getEventFunFact(eventId: number): Observable<FunFactCardDTO> {
    return this.http.get<FunFactCardDTO>(`${this.apiUrl}/event/${eventId}`);
  }
}
