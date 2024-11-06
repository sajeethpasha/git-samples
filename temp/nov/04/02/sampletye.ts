<ng-template appCellDef="campaignDetails" let-element>
  <div class="campaign-details-dropdown-container" (click)="element.isExpanded = !element.isExpanded">
    <span class="menu-more-focus" tabindex="0">
      Campaign Details
      <img src="assets/img/arrow-expand_2.png" alt="expand" class="material-icons" />
    </span>

    <!-- Dropdown content -->
    <ul *ngIf="element.isExpanded && element.campaignDetails?.length > 0" class="campaign-details-list">
      <li *ngFor="let detail of element.campaignDetails">
        {{ detail.campaignId }} - {{ detail.campaignName }}
      </li>
    </ul>
  </div>
</ng-template>
