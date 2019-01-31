import React from 'react';
import { MainLayout, ContentEditableInput } from 'components';
import EditSection from "../../components/EditSection";

class Edit extends React.Component {
  constructor(props) {
    super(props);

    this.onChange = this.onChange.bind(this);

    const ota = this.props.OTA || {};

    this.state = {
      OTA: {
        orgId: this.props.orgId,
        productId: undefined,
        pathToFirmware: '',
        firmwareOriginalFileName: '',
        deviceIds: [],
        title: ota.title || 'New Shipping',
        checkBoardType: false,
        firmwareInfo: {
          version: '',
          boardType: '',
          buildDate: '',
          md5Hash: '',
        },
        attemptsLimit: 0,
        isSecure: false
      }
    };
  }

  onChange(value) {
    const { OTA } = this.state;
    OTA.title = value;
    this.setState({ OTA });
  }

  render() {
    const { OTA } = this.state;
    return (
      <MainLayout>
        <MainLayout.Header
          title={<ContentEditableInput maxLength={40}
                                       value={OTA.title}
                                       onChange={this.onChange}/>
          }>
        </MainLayout.Header>

        <MainLayout.Content className="organizations-create-content">
          <EditSection title={'Target selection'}>
            <div>test</div>
          </EditSection>
          <EditSection title={'Firmware'}>
            <div>test</div>
          </EditSection>
          <EditSection title={'Firmware'}>
            <div>test</div>
          </EditSection>
          <EditSection title={'Review and start'}>
            <div>test</div>
          </EditSection>

        </MainLayout.Content>
      </MainLayout>
    )
      ;
  }

}

export default Edit;
