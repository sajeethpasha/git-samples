import { Component, Injector, OnInit } from '@angular/core';
import {
  SearchTableColumn,
  SearchField,
  SearchFieldType,
} from 'vcmp-common-ui/src/app/shared/search-table/search-table.model';
import { AbstractListPageComponent } from 'vcmp-common-ui/src/app/shared/abstract-components/abstract-list-page.component';
import { ButtonConfig } from 'vcmp-common-ui/src/app/shared/buttons/button.model';
import { Icon } from 'app/core/model/icon.model';
import { EmailService } from 'app/core/service/home/communication/email.service';
import { StatusCode } from 'app/core/model/status.model';
import { RoutingPath } from 'app/core/routing/routing-path';
import { PageTransferData } from 'app/core/model/page-transfer-data.model';
import { Mode } from 'vcmp-common-ui/src/app/core/model/core.model';
import { PageTransferDataService } from 'app/core/service/page-transfer-data.service';

@Component({
  selector: 'app-email-search',
  templateUrl: './email-search.component.html',
  styleUrls: ['./email-search.component.scss']
})
export class EmailSearchComponent extends AbstractListPageComponent implements OnInit {
  StatusCode = StatusCode;

  buttonConfigs: ButtonConfig[] = [
    {
      label: 'Create Email',
      iconConfig: {
        icon: Icon.ADD_LIGHT,
        location: 'left'
      },
      disabled: () => false,
      onClick: () => this.router.navigate(['campaign-management/email/create'])
    }
  ];

  advancedSearchFields: Array<SearchField>;
  quickSearchField: SearchField;
  columns: Array<SearchTableColumn> = [
    { label: 'Communication Name', key: 'templateName' },
    { label: 'Message', key: 'subject' },
    { label: 'Status', key: 'status', type: 'status' },
    {
      label: 'Campaign Details',
      key: 'campaignDetails'
    },
    { label: 'Create Date', key: 'templateCreatedDate', type: 'date' },
    { label: 'Last Date', key: 'templateLastModifiedDate', type: 'date' }
  ];

  constructor(
    private emailService: EmailService,
    private pageTransferDataService: PageTransferDataService,
    injector: Injector
  ) {
    super(injector);
  }

  ngOnInit() {
    this.addSearchFilters();
  }

  addSearchFilters() {
    this.quickSearchField = { label: 'Search Email', key: 'text' };

    this.advancedSearchFields = [
      { label: 'Modified Date From', key: 'updatedDateFrom', mappedKey: 'templateLastModifiedDate', type: SearchFieldType.FROM_DATE },
      { label: 'Modified Date To', key: 'updatedDateTo', mappedKey: 'templateLastModifiedDate', type: SearchFieldType.TO_DATE },
      {
        label: 'Campaign Details',
        key: 'campaignDetails',
        type: SearchFieldType.DROPDOWN,
        options: [
          { label: 'PROD', value: 'PROD' },
          { label: 'TEST', value: 'TEST' },
          { label: 'INACTIVE', value: 'INACTIVE' },
          { label: 'HOLD', value: 'HOLD' },
          { label: 'EXPIRED', value: 'EXPIRED' }
        ]
      }
    ];
  }

  search(filters: any) {
    return this.emailService.search(filters);
  }

  navigateToView(element: any) {
    const pageTransferData = new PageTransferData();
    pageTransferData.dataObject = element;
    pageTransferData.mode = Mode.VIEW;
    this.pageTransferDataService.setPageTransferData(pageTransferData);
    const path = `${RoutingPath.ROOT}/${RoutingPath.EMAIL.MANAGE_PATH}`;
    this.router.navigate([path]);
  }

  toggleDropdown(element: any) {
    console.log("toggleDropdown:" + JSON.stringify(element));
    element.isExpanded = !element.isExpanded;
  }
}
