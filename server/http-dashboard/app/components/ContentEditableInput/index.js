import React from 'react';
import PropTypes from 'prop-types';
import AutosizeInput from 'react-input-autosize';
import {Button} from 'antd';
import './styles.less';

class ContentEditableInput extends React.Component {

  static propTypes = {
    value: PropTypes.string,
    onChange: PropTypes.func,
    width: PropTypes.number,
    maxLength: PropTypes.number,
    style: PropTypes.object,
    toolSize: PropTypes.string,
  };

  constructor(props) {
    super(props);

    this.state = {
      value: this.props.value || '',
      isEditMode: false,
    };

    this.handleEdit = this.handleEdit.bind(this);
    this.handleSave = this.handleSave.bind(this);
    this.handleChange = this.handleChange.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    this.handleKeyPress = this.handleKeyPress.bind(this);

  }

  componentDidUpdate(prevProps) {
    if(prevProps.value !== this.props.value) {
      this.handleChange({
        target: {
          value: this.props.value
        }
      });
    }
  }

  handleChange(e) {
    this.setState({
      value: e.target.value
    });
  }

  handleSave() {
    this.setState({
      isEditMode: false
    });

    if(!this.state.value || !(this.state.value.trim())) {
      this.handleCancel();
    } else {
      this.props.onChange(this.state.value.trim());

      this.setState({
        value: this.state.value.trim()
      });
    }

  }

  handleCancel() {
    this.setState({
      isEditMode: false,
      value: this.props.value,
    });
  }

  handleEdit() {
    this.setState({
      isEditMode: true
    });
  }

  handleKeyPress(e) {
    if (e.key === 'Enter') {
      this.handleSave();
    }
  }

  isValid() {
    return true;
  }

  render() {

    const MAX_LENGTH = 100;

    return (
      <div className="content-editable-input">

        { !this.state.isEditMode ? (
          <div>
            <span className="content-editable-input--value">{this.state.value}</span>

            <Button size={this.props.toolSize || 'default'} icon="edit"
                    className="user-profile--my-account-edit-button content-editable-input--btn"
                    onClick={this.handleEdit}/>
          </div>
        ) : (
          <div>
            <AutosizeInput maxLength={this.props.maxLength || MAX_LENGTH} onKeyPress={this.handleKeyPress} className="content-editable-input--input" value={this.state.value} onChange={this.handleChange}/>

            <Button icon="save" size={this.props.toolSize || 'default'} className="user-profile--my-account-edit-button content-editable-input--btn"
                    onClick={this.handleSave} disabled={!this.isValid()}/>&nbsp;
            <Button icon="close" size={this.props.toolSize || 'default'} className="user-profile--my-account-edit-button content-editable-input--btn"
                    onClick={this.handleCancel}/>
          </div>
        )}

      </div>
    );
  }

}

export default ContentEditableInput;
