import { Component } from '@angular/core';
import {TuiTitle} from "@taiga-ui/core";
import {TuiHeader} from "@taiga-ui/layout";

@Component({
  selector: 'app-about',
  standalone: true,
    imports: [
        TuiHeader,
        TuiTitle
    ],
  templateUrl: './about.component.html',
  styleUrl: './about.component.scss'
})
export class AboutComponent {

}
