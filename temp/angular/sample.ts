import { Component } from '@angular/core';

@Component({
  selector: 'app-campaign-details',
  templateUrl: './campaign-details.component.html',
  styleUrls: ['./campaign-details.component.scss']
})
export class CampaignDetailsComponent {
  campaigns = [
    { name: 'Campaign-Name', link: '/campaign/1' },
    { name: 'Campaign-Name-with-extra-length', link: '/campaign/2' },
    { name: 'Campaign-Name-with-extra-length', link: '/campaign/3' },
    { name: 'Campaign-Name-with-extra-length', link: '/campaign/4' },
  ];

  get displayCampaignText() {
    if (this.campaigns.length > 1) {
      return `${this.campaigns[0].name} (+${this.campaigns.length - 1})`;
    }
    return this.campaigns[0]?.name || 'No Campaigns';
  }

  /**
   * Helper method to format the campaign details for display.
   * @param details Array of campaign objects.
   * @returns A formatted string of campaign names.
   */
  formatCampaignDetails(details: any[]): string {
    return details.map(detail => detail.name).join(', ');
  }
}
