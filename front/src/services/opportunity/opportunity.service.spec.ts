import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';

import { OpportunityService } from './opportunity.service';

describe('OpportunityService', () => {
  let service: OpportunityService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient()] });
    service = TestBed.inject(OpportunityService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
