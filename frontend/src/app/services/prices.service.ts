import {HttpClient} from '@angular/common/http';
import {SERVER_ADDRESS} from '../globals';
import {computed, inject, Injectable, signal} from '@angular/core';
import {Price} from '../user_db.dto/price.dto';


@Injectable({ providedIn: 'root' })
export class PricesService {
    private httpClient = inject(HttpClient);
    private PATH = SERVER_ADDRESS + 'prices'
    private pricesSignal = signal<Price[]>([]);

    prices = computed(() => this.pricesSignal());

    getPricesByLocation(locationId: number) {
        this.httpClient.get<Price[]>(this.PATH + `/find/location_id=${locationId}`).subscribe((data) => {
            this.pricesSignal.set(data);
        });
    }

    createOrUpdate(price: Price) {
        this.httpClient.post<Price>(this.PATH + '/create', price).subscribe({
            next: (data: Price) => {this.updatePrices(data);},
            error: (e) => {console.log('Error!', e)}
        });
    }

    delete(id: number): void {
        this.httpClient.delete<Price>(this.PATH + '/id=' + id).subscribe({
            next: (data: Price) => {
                console.log('Success!', data);
                const updatedPrices = this.prices().filter(price => price.priceId !== id);
                this.pricesSignal.set(updatedPrices);
            },
            error: (e) => {console.log('Error!', e)}
        });
    }

    updatePrices(price: Price) {
        const currentPrices = this.prices();
        const existingIndex = currentPrices.findIndex(p => p.priceId === price.priceId);

        if (existingIndex === -1) {
            this.pricesSignal.set([...currentPrices, price]);
        } else {
            const updatedPrices = currentPrices.map((p, index) =>
                index === existingIndex ? price : p
            );
            this.pricesSignal.set(updatedPrices);
        }
    }

}
