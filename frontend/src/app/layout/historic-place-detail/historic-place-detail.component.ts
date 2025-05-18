import { Component, Input } from '@angular/core';
import {CommonModule, DecimalPipe, NgIf} from '@angular/common';
import {HistoricalPlaceEntity} from '../../dto/db_entity/HistoricalPlaceEntity';

@Component({
  selector: 'app-historic-place-detail',
  imports: [
    NgIf,
    CommonModule
  ],
  templateUrl: './historic-place-detail.component.html',
  styleUrl: './historic-place-detail.component.scss'
})
export class HistoricPlaceDetailComponent {
  @Input()
  get selectedPlace(): any {
    return this._selectedPlace;
  }
  set selectedPlace(value: any) {
    this._selectedPlace = value;
  }
  private _selectedPlace: any;

  images: string[] = [];

  currentImageIndex: number = 0;

  nextImage(){
    this.currentImageIndex++;
  }

  previousImage() {
    this.currentImageIndex--;
  }

  setImage(index: number) {
    this.currentImageIndex = index;
  }
}
