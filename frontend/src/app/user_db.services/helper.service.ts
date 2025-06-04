import {Injectable} from '@angular/core';

@Injectable({providedIn: 'root'})
export class HelperService {

  sanitizeDate(raw: string | Date): Date | null {
    if (raw instanceof Date) return raw;

    const cleanedLower = raw.replace(/\s?(am|pm)/, '');
    const cleanedUpper = cleanedLower.replace(/\s?(AM|PM)/, '');
    const date = new Date(cleanedUpper);
    return isNaN(date.getTime()) ? null : date;

  }

}
