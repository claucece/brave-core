// Copyright (c) 2023 The Brave Authors. All rights reserved.
// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this file,
// You can obtain one at https://mozilla.org/MPL/2.0/.

import * as React from 'react'
import { getLocale } from '../../../../common/locale'
import { BraveWallet } from '../../../constants/types'

import {
  StyledWrapper,
  ContentWrapper,
  UploadIcon,
  CloseIcon,
  CheckIcon
} from './nft-pinning-status.style'

interface Props {
  pinningStatusCode: BraveWallet.TokenPinStatusCode
}

export const NftPinningStatus = (props: Props) => {
  const { pinningStatusCode } = props
  // state
  const [icon, setIcon] = React.useState<React.ReactNode>()
  const [message, setmessage] = React.useState<string>('')

  // effects
  React.useEffect(() => {
    switch (pinningStatusCode) {
      case BraveWallet.TokenPinStatusCode.STATUS_PINNING_IN_PROGRESS:
      case BraveWallet.TokenPinStatusCode.STATUS_PINNING_PENDING:
        setmessage(getLocale('braveWalletNftPinningStatusPinning'))
        setIcon(<UploadIcon />)
        break

      case BraveWallet.TokenPinStatusCode.STATUS_PINNING_FAILED:
        setmessage(getLocale('braveWalletNftPinningStatusPinningFailed'))
        setIcon(<CloseIcon />)
        break

      case BraveWallet.TokenPinStatusCode.STATUS_PINNED:
        setmessage(getLocale('braveWalletNftPinningStatusPinned'))
        setIcon(<CheckIcon />)
        break
    }
  }, [pinningStatusCode])

  return (
    <StyledWrapper>
      <ContentWrapper
        pinningStatus={pinningStatusCode}
      >
        {icon}
        {message}
      </ContentWrapper>
    </StyledWrapper>
  )
}
