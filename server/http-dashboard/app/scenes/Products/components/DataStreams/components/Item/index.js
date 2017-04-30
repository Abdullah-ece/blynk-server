import React from 'react';
import classnames from 'classnames';
import {Row, Col} from 'antd';
import {
  MetadataSelect as MetadataFormSelect
} from 'components/Form';
import {Form, reduxForm} from 'redux-form';
import Preview from 'scenes/Products/components/Preview';
import FormItem from 'components/FormItem';

@reduxForm({
  touchOnChange: true
})
class DataStreamItem extends React.Component {
  static propTypes = {
    anyTouched: React.PropTypes.bool,
    invalid: React.PropTypes.bool,
    preview: React.PropTypes.object,
    form: React.PropTypes.string,
    fields: React.PropTypes.object,
    children: React.PropTypes.any,
    onDelete: React.PropTypes.func,
    touchFormById: React.PropTypes.func,
    onClone: React.PropTypes.func,
    touched: React.PropTypes.bool,
    updateMetadataFieldInvalidFlag: React.PropTypes.func
  };

  handleSubmit() {

  }

  preview() {

    if (!this.props.anyTouched && !this.props.preview.name) {
      return null;
    }

    if (this.props.invalid) {
      return (<Preview> <Preview.Unavailable /> </Preview>);
    }

    return (
      <Preview>
        <Preview.Name>{this.props.preview.name}</Preview.Name>
        <Preview.Value>{this.props.preview.value || 'Empty'}</Preview.Value>
      </Preview>
    );

  }

  render() {
    const itemClasses = classnames({
      'product-metadata-item': true,
      'product-metadata-item-active': /*this.state.isActive*/false,
    });

    return (
      <div className={itemClasses}>
        <Form onSubmit={this.handleSubmit.bind(this)}>
          <Row gutter={8}>
            <Col span={13}>
              {this.props.children}
            </Col>
            <Col span={2}>

              <FormItem.TitleGroup>
                <FormItem.Title>PIn</FormItem.Title>
              </FormItem.TitleGroup>
              <FormItem.Content>
                <MetadataFormSelect name="units" type="text" placeholder="Pin"
                                    dropdownClassName="product-metadata-item-unit-dropdown" values={this.Unit}/>
              </FormItem.Content>
            </Col>
            <Col span={8}>
              { this.preview() }
            </Col>
          </Row>
          <div className="product-metadata-item-tools">
            {/*<DragHandler/>*/}
            {/*{deleteButton}*/}
            {/*<Button icon="copy" size="small" onClick={this.props.onClone.bind(this)}/>*/}
          </div>
        </Form>
      </div>
    );
  }
}

export default DataStreamItem;
