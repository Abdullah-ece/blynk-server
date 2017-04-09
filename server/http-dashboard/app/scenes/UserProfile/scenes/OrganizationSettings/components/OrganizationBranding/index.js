import React from 'react';

import './styles.scss';
import LogoUploader from "components/LogoUploader/index";
import BrandingColorPicker from "components/BrandingColorPicker/index";
import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';
import {OrganizationBrandingUpdate, OrganizationSave} from 'data/Organization/actions';

@connect((state) => ({
  primaryColor: state.Organization.primaryColor,
  secondaryColor: state.Organization.secondaryColor,
  Organization: state.Organization,
}), (dispatch) => ({
  OrganizationBrandingUpdate: bindActionCreators(OrganizationBrandingUpdate, dispatch),
  OrganizationSave: bindActionCreators(OrganizationSave, dispatch)
}))
export default class OrganizationBranding extends React.Component {
  static propTypes = {
    Organization: React.PropTypes.object,
    primaryColor: React.PropTypes.any,
    secondaryColor: React.PropTypes.any,
    OrganizationBrandingUpdate: React.PropTypes.func,
    OrganizationSave: React.PropTypes.func
  };

  onPrimaryColorChange(primaryColor) {
    const data = {
      primaryColor: primaryColor,
      secondaryColor: this.props.secondaryColor
    };
    this.props.OrganizationBrandingUpdate(data);
    this.props.OrganizationSave(Object.assign({}, this.props.Organization, data));
  }

  onSecondaryColorChange(secondaryColor) {
    const data = {
      primaryColor: this.props.primaryColor,
      secondaryColor: secondaryColor
    };
    this.props.OrganizationBrandingUpdate(data);
    this.props.OrganizationSave(Object.assign({}, this.props.Organization, data));
  }

  render() {
    return (
      <div className="user-profile--organization-settings--organization-branding">
        <LogoUploader/>
        <div className="user-profile--organization-settings--organization-branding-colors">
          <BrandingColorPicker title="primary color" color={this.props.primaryColor}
                               onChange={this.onPrimaryColorChange.bind(this)}/>
          <BrandingColorPicker title="secondary color" color={this.props.secondaryColor}
                               onChange={this.onSecondaryColorChange.bind(this)}/>
        </div>
      </div>
    );
  }
}
