import { Component, Injector, OnInit } from '@angular/core';

@Component({
  selector: 'app-email-search',
  templateUrl: './email-search.component.html',
  styleUrls: ['./email-search.component.scss'],
})
export class EmailSearchComponent extends AbstractListPageComponent implements OnInit {
  campaigns = [
    { campaignId: '123', campaignName: 'Holiday Campaign' },
    { campaignId: '456', campaignName: 'Spring Sale' },
    { campaignId: '789', campaignName: 'Back to School' },
  ];

  constructor(
    private injector: Injector,
    // other services here
  ) {
    super(injector);
  }

  ngOnInit() {
    // Your initialization code here
  }

  transformCampaignDetailsForRow(campaignDetails: any[]): any[] {
    return campaignDetails.map((detail) => ({
      label: detail.campaignName,
      value: detail.campaignId,
    }));
  }

  onSubTenantChange() {
    console.log("Sub-tenant changed.");
  }
}
