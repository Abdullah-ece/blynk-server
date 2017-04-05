import React from 'react';

import './styles.scss';
import LogoUploader from "components/LogoUploader/index";

export default class OrganizationBranding extends React.Component {
  render() {
    return (
      <div>
        <LogoUploader/>
      </div>
    );
  }
}
