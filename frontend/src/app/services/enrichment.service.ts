import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {OPENAI_ADDRESS} from '../globals';
import {EnrichmentResponse} from '../dto/enrichmentResponse.dto';
import {Observable} from 'rxjs';
import {SummaryResponse} from '../dto/summaryResponse.dto';

@Injectable({ providedIn: 'root' })
export class EnrichmentService {
  private readonly enrichmentPath = 'api/enrich/full';
  private readonly summaryPath = 'api/enrich/summary';

  constructor(readonly httpClient: HttpClient) {}

  enrichContentWithTone(tone: string, content: string): Observable<EnrichmentResponse> {
    const payload = { tone, content };
    return this.httpClient.post<EnrichmentResponse>(
      `${OPENAI_ADDRESS}${this.enrichmentPath}`,
      payload
    );
  }

  generateSummary(content: string): Observable<SummaryResponse> {
    const payload = { content };
    return this.httpClient.post<SummaryResponse>(
      `${OPENAI_ADDRESS}${this.summaryPath}`,
      payload
    );
  }

}
