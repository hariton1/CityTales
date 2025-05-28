import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class UtilitiesService {

  constructor() { }

  /**
   * Converts a date to a formatted string.
   * @param date - The date to format.
   * @returns The formatted date string.
   */
  formatDate(date: Date): string {
    // Format as YYYY-MM-DDTHH:mm:ss.000+00:00
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0'); // Months are 0-indexed
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');

    // Format the date string exactly as required by the backend
    return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}.000+02:00`;
  }

}
