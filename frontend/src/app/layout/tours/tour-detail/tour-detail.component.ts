import {Component, inject} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {TourDto} from '../../../dto/tour.dto';
import {TourService} from '../../../services/tour.service';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {TuiAlertService, TuiButton, TuiLoader, TuiTextfield, TuiTitle} from '@taiga-ui/core';
import {CommonModule} from '@angular/common';
import {BuildingEntity} from '../../../dto/db_entity/BuildingEntity';
import {TuiCard} from '@taiga-ui/layout';
import {firstValueFrom} from 'rxjs';
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';
import {PriceEntity} from '../../../dto/tour_entity/PriceEntity';

@Component({
  selector: 'app-tour-detail',
  imports: [
    TuiTextfield,
    ReactiveFormsModule,
    CommonModule,
    TuiTitle,
    TuiButton,
    TuiCard,
    TuiLoader
  ],
  templateUrl: './tour-detail.component.html',
  styleUrl: './tour-detail.component.scss'
})
export class TourDetailComponent {

  tourId: number = 0;
  tour: TourDto = new TourDto(0, '', '', 0, 0, 0, 0, [], 0, 0, 'NONE', 0, new Map());
  private tourService: TourService;
  private router: Router;
  private readonly alerts = inject(TuiAlertService);
  pricePerStop: Map<number, PriceEntity[]> = new Map();
  private filter_urls: string[] = ["https://www.geschichtewiki.wien.gv.at/KnowledgeWiki.png",
    "https://www.geschichtewiki.wien.gv.at/extensions/SemanticMediaWiki/res/smw/logo_footer.png",
    "https://www.geschichtewiki.wien.gv.at/KnowledgeWiki.png",
    "https://www.geschichtewiki.wien.gv.at/skins/wienwgw4/img/wgw_logo_10.png",
    "https://www.geschichtewiki.wien.gv.at/images/7/7d/RDF.png"];

  nameControl = new FormControl('');
  descriptionControl = new FormControl('');

  isLoading = true;

  constructor(
    private route: ActivatedRoute,
    tourService: TourService,
    router: Router,) {
    this.route.params.subscribe(params => {
      this.tourId = params['id'];
    });
    this.tourService = tourService;
    this.router = router;

  this.tourService.getTourForTourId(this.tourId).subscribe(async tour => {
    try{
      this.tour = TourDto.fromTourEntity(tour);
      this.pricePerStop = this.tour.getPricePerStop();
      this.nameControl.setValue(this.tour.getName());
      this.descriptionControl.setValue(this.tour.getDescription());
      await this.recalculateStopDistances();
      console.log(this.tour)

      this.tour.getStops().forEach(stop => {
        stop.imageUrls = stop.imageUrls.filter(url => !this.filter_urls.includes(url));
      })
    } catch (error) {
      console.log(error);
    } finally {
      this.isLoading = false;
    }
  })};

  saveChanges() {
    this.tour.setName(this.nameControl.value!);
    this.tour.setDescription(this.descriptionControl.value!);
    this.tourService.updateTour(this.tour).subscribe({
      next: tour => {
        this.alerts.open('Your tour is updated!', {
          label: 'Success!',
          appearance: 'success',
          autoClose: 3000
        }).subscribe();
      },
      error: any => {
        console.log('An error occurred when updating tour!');
        this.alerts.open('Your tour could not be updated.', {
          label: 'Failure!',
          appearance: 'warning',
          autoClose: 3000
        }).subscribe();
      }
    });
  }

  async deleteStop(stop: BuildingEntity) {
    this.tour.getStops().splice(this.tour.getStops().indexOf(stop), 1);
    this.updateEstimate();
    await this.recalculateStopDistances();
  }

  deleteTour() {
    this.tourService.deleteTourById(this.tourId);
    this.router.navigateByUrl('/tours');
  }

  updateEstimate() {
    this.tourService
      .getDurationDistanceEstimate(
        this.tour.getStart_lat(),
        this.tour.getStart_lng(),
        this.tour.getEnd_lat(),
        this.tour.getEnd_lng(),
        this.tour.getStops()
      )
      .subscribe(estimate => {
        console.log(estimate.duration);
        console.log(estimate.distance);
        this.tour = new TourDto(
          this.tour.getId(),
          this.tour.getName(),
          this.tour.getDescription(),
          this.tour.getStart_lat(),
          this.tour.getStart_lng(),
          this.tour.getEnd_lat(),
          this.tour.getEnd_lng(),
          this.tour.getStops(),
          estimate.distance,
          estimate.duration,
          this.tour.getUserId(),
          this.tour.getTourPrice(),
          this.tour.getPricePerStop()
        );
        console.log(this.tour.getDistance());
        console.log(this.tour.getDurationEstimate());
      });
  }

  distanceBetweenStops: number[] = [];
  durationBetweenStops: number[] = [];

  async recalculateStopDistances() {
    this.distanceBetweenStops = [];
    this.durationBetweenStops = [];

    const stops = this.tour.getStops();

    // Start to first stop
    if (stops.length > 0) {
      const distance = await this.getDistanceBetweenStops(-1, 0);
      const duration = await this.getDurationBetweenStops(-1, 0);

      this.distanceBetweenStops.push(distance);
      this.durationBetweenStops.push(duration);
    }

    // Between intermediate stops
    for (let i = 0; i < stops.length - 1; i++) {
      const distance = await this.getDistanceBetweenStops(i, i + 1);
      const duration = await this.getDurationBetweenStops(i, i + 1);

      this.distanceBetweenStops.push(distance);
      this.durationBetweenStops.push(duration);
    }

    // Last stop to end
    if (stops.length > 0) {
      const distance = await this.getDistanceBetweenStops(stops.length - 1, stops.length);
      const duration = await this.getDurationBetweenStops(stops.length - 1, stops.length);

      this.distanceBetweenStops.push(distance);
      this.durationBetweenStops.push(duration);
    }

    console.log('Distances:', this.distanceBetweenStops);
    console.log('Durations:', this.durationBetweenStops);
  }

  async getDistanceBetweenStops(index1: number, index2: number): Promise<number> {
    console.log("Invoked distance computation between stops");

    let lat1: number, lng1: number, lat2: number, lng2: number;

    // Start to first stop
    if (index1 === -1) {
      lat1 = this.tour.getStart_lat();
      lng1 = this.tour.getStart_lng();
      lat2 = this.tour.getStops()[index2].latitude;
      lng2 = this.tour.getStops()[index2].longitude;
    }
    // Last stop to end
    else if (index2 === this.tour.getStops().length) {
      lat1 = this.tour.getStops()[index1].latitude;
      lng1 = this.tour.getStops()[index1].longitude;
      lat2 = this.tour.getEnd_lat();
      lng2 = this.tour.getEnd_lng();
    }
    // Between two stops
    else {
      lat1 = this.tour.getStops()[index1].latitude;
      lng1 = this.tour.getStops()[index1].longitude;
      lat2 = this.tour.getStops()[index2].latitude;
      lng2 = this.tour.getStops()[index2].longitude;
    }

    const result = await firstValueFrom(
      this.tourService.getDurationDistanceEstimate(lat1, lng1, lat2, lng2, [])
    );

    return result.distance; // in km
  }

  async getDurationBetweenStops(index1: number, index2: number): Promise<number> {
    let lat1: number, lng1: number, lat2: number, lng2: number;

    if (index1 === -1) {
      lat1 = this.tour.getStart_lat();
      lng1 = this.tour.getStart_lng();
      lat2 = this.tour.getStops()[index2].latitude;
      lng2 = this.tour.getStops()[index2].longitude;
    } else if (index2 === this.tour.getStops().length) {
      lat1 = this.tour.getStops()[index1].latitude;
      lng1 = this.tour.getStops()[index1].longitude;
      lat2 = this.tour.getEnd_lat();
      lng2 = this.tour.getEnd_lng();
    } else {
      lat1 = this.tour.getStops()[index1].latitude;
      lng1 = this.tour.getStops()[index1].longitude;
      lat2 = this.tour.getStops()[index2].latitude;
      lng2 = this.tour.getStops()[index2].longitude;
    }

    const result = await firstValueFrom(
      this.tourService.getDurationDistanceEstimate(lat1, lng1, lat2, lng2, [])
    );

    return result.duration*60; // in minutes
  }

  exportSiteToPdf() {
    const element = document.getElementById('pdf-content');

    if (!element) return;

    html2canvas(element, {
      scrollY: -window.scrollY,
      scale: 2,
      useCORS: false
    }).then((canvas) => {
      const imgData = canvas.toDataURL('image/png');

      const pdf = new jsPDF('p', 'mm', 'a4');
      const pageWidth = pdf.internal.pageSize.getWidth();
      const pageHeight = pdf.internal.pageSize.getHeight();

      const imgWidth = pageWidth;
      const imgHeight = (canvas.height * pageWidth) / canvas.width;

      let heightLeft = imgHeight;
      let position = 30;

      const tourName = this.tour.getName();
      const tourNameSafe = tourName.replace(/[^a-z0-9]/gi, '_').toLowerCase();
      const currentDate = new Date().toLocaleDateString();


      pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight);
      heightLeft -= pageHeight;

      pdf.setFontSize(16);
      pdf.text(tourName, pageWidth / 2, 15, { align: 'center' });

      pdf.setFontSize(10);
      pdf.text(currentDate, pageWidth - 10, 10, { align: 'right' });

      while (heightLeft > 0) {
        position = heightLeft - imgHeight;
        pdf.addPage();
        pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight);
        heightLeft -= pageHeight;
      }

      pdf.save(`${tourNameSafe || 'tour-detail'}.pdf`);
    });
  }

  generateGoogleMapsTourLink() {
    const stops = this.tour.getStops();

    const locations: string[] = [];

    locations.push(`${this.tour.getStart_lat()},${this.tour.getStart_lng()}`);

    for (const stop of stops) {
      locations.push(`${stop.latitude},${stop.longitude}`);
    }

    locations.push(`${this.tour.getEnd_lat()},${this.tour.getEnd_lng()}`);

    const baseUrl = 'https://www.google.com/maps/dir/';
    const url = baseUrl + locations.map(encodeURIComponent).join('/');

    window.open(url, '_blank');
  }
}
