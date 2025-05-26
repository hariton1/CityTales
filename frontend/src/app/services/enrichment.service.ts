import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {OPENAI_ADDRESS} from '../globals';
import {EnrichmentResponse} from '../dto/enrichmentResponse.dto';
import {Observable} from 'rxjs';

@Injectable({ providedIn: 'root' })
export class EnrichmentService {
  readonly enrichmentPath = 'api/enrich';

  constructor(readonly httpClient: HttpClient) {}

  enrichContentWithTone(tone: string, content: string) : Observable<EnrichmentResponse> {
    const payload = {
      tone,
      content
    };

    return this.httpClient.post<EnrichmentResponse>(OPENAI_ADDRESS.concat(this.enrichmentPath), payload);
  }
}
